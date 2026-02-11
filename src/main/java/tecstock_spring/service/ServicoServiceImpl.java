package tecstock_spring.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tecstock_spring.controller.ServicoController;
import tecstock_spring.exception.NomeDuplicadoException;
import tecstock_spring.model.Empresa;
import tecstock_spring.model.OrdemServico;
import tecstock_spring.model.Servico;
import tecstock_spring.repository.EmpresaRepository;
import tecstock_spring.repository.OrdemServicoRepository;
import tecstock_spring.repository.ServicoRepository;
import tecstock_spring.util.TenantContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicoServiceImpl implements ServicoService {

    private final ServicoRepository repository;
    private final EmpresaRepository empresaRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    Logger logger = LoggerFactory.getLogger(ServicoController.class);

    @Override
    public Servico salvar(Servico servico) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        validarNomeDuplicado(servico.getNome(), null, empresaId);
        
        Empresa empresa = empresaRepository.findById(empresaId)
            .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        servico.setEmpresa(empresa);
        
        Servico servicoSalvo = repository.save(servico);
        logger.info("Serviço salvo com sucesso na empresa " + empresaId + ": " + servicoSalvo);
        return servicoSalvo;
    }

    @Override
    public Servico buscarPorId(Long id) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        return repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado ou não pertence à sua empresa"));
    }

    @Override
    public List<Servico> listarTodos() {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        List<Servico> servicos = repository.findByEmpresaId(empresaId);
        if (servicos.isEmpty()) {
            logger.info("Nenhum serviço cadastrado na empresa " + empresaId);
        } else {
            logger.info(servicos.size() + " serviços encontrados na empresa " + empresaId);
        }
        return servicos;
    }

    @Override
    @SuppressWarnings("null")
    public Servico atualizar(Long id, Servico novoServico) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        Servico servicoExistente = repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado ou não pertence à sua empresa"));
        
        validarNomeDuplicado(novoServico.getNome(), id, empresaId);
        BeanUtils.copyProperties(novoServico, servicoExistente, "id", "empresa", "createdAt", "updatedAt");
        return repository.save(servicoExistente);
    }

    @Override
    @SuppressWarnings("null")
    public void deletar(Long id) {
        repository.deleteById(id);
    }
    
    @Override
    public List<Servico> listarComPendentes() {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        logger.info("Listando serviços com unidades pendentes em OSs não encerradas da empresa " + empresaId);
        List<Servico> servicos = repository.findByEmpresaId(empresaId);
        return servicos.stream()
                .filter(servico -> servico.getUnidadesUsadasEmOS() != null && servico.getUnidadesUsadasEmOS() > 0)
                .collect(Collectors.toList());
    }
    
    @Override
    public void atualizarUnidadesUsadas() {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        logger.info("Atualizando unidades usadas em OSs para todos os serviços da empresa " + empresaId);
        
        List<Servico> todosServicos = repository.findByEmpresaId(empresaId);
        List<OrdemServico> osNaoEncerradas = ordemServicoRepository.findByStatusNotAndEmpresaId("Encerrada", empresaId);
        
        for (Servico servico : todosServicos) {
            int unidadesUsadas = 0;
            
            for (OrdemServico os : osNaoEncerradas) {
                if (os.getServicosRealizados() != null) {
                    long count = os.getServicosRealizados().stream()
                            .filter(s -> s.getId().equals(servico.getId()))
                            .count();
                    unidadesUsadas += count;
                }
            }
            
            servico.setUnidadesUsadasEmOS(unidadesUsadas);
            repository.save(servico);
            
            if (unidadesUsadas > 0) {
                logger.info("Serviço '" + servico.getNome() + "' tem " + unidadesUsadas + " unidades em OSs não encerradas");
            }
        }
        
        logger.info("Atualização de unidades usadas concluída");
    }
    
    private void validarNomeDuplicado(String nome, Long idExcluir, Long empresaId) {
        logger.info("Validando nome duplicado para serviço: " + nome + " (excluindo ID: " + idExcluir + ") na empresa " + empresaId);
        
        if (nome == null || nome.trim().isEmpty()) {
            logger.warn("Nome do serviço é nulo ou vazio");
            return;
        }
        
        String nomeLimpo = nome.trim();
        logger.info("Nome limpo para validação: " + nomeLimpo);
        
        boolean exists;
        if (idExcluir != null) {
            exists = repository.existsByNomeIgnoreCaseAndIdNotAndEmpresaId(nomeLimpo, idExcluir, empresaId);
            logger.info("Verificação para atualização - Existe outro serviço com nome " + nomeLimpo + " (excluindo ID " + idExcluir + ") na empresa " + empresaId + ": " + exists);
        } else {
            exists = repository.existsByNomeIgnoreCaseAndEmpresaId(nomeLimpo, empresaId);
            logger.info("Verificação para criação - Existe serviço com nome " + nomeLimpo + " na empresa " + empresaId + ": " + exists);
        }
        
        if (exists) {
            String mensagem = "Nome do serviço já está cadastrado nesta empresa";
            logger.error(mensagem + ": " + nomeLimpo);
            throw new NomeDuplicadoException(mensagem);
        }
        
        logger.info("Validação de nome concluída com sucesso para serviço: " + nomeLimpo);
    }
}