package tecstock_spring.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tecstock_spring.controller.ServicoController;
import tecstock_spring.exception.NomeDuplicadoException;
import tecstock_spring.model.OrdemServico;
import tecstock_spring.model.Servico;
import tecstock_spring.repository.OrdemServicoRepository;
import tecstock_spring.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicoServiceImpl implements ServicoService {

    private final ServicoRepository repository;
    private final OrdemServicoRepository ordemServicoRepository;
    Logger logger = Logger.getLogger(ServicoController.class);

    @Override
    public Servico salvar(Servico servico) {
        validarNomeDuplicado(servico.getNome(), null);
        Servico servicoSalvo = repository.save(servico);
        logger.info("Serviço salvo com sucesso: " + servicoSalvo);
        return servicoSalvo;
    }

    @Override
    public Servico buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
    }

    @Override
    public List<Servico> listarTodos() {
        List<Servico> servicos = repository.findAll();
        if (servicos.isEmpty()) {
            logger.info("Nenhum serviço cadastrado.");
        } else {
            logger.info(servicos.size() + " serviços encontrados.");
        }
        return servicos;
    }

    @Override
    public Servico atualizar(Long id, Servico novoServico) {
        Servico servicoExistente = buscarPorId(id);
        validarNomeDuplicado(novoServico.getNome(), id);
        BeanUtils.copyProperties(novoServico, servicoExistente, "id", "createdAt", "updatedAt");
        return repository.save(servicoExistente);
    }

    @Override
    public void deletar(Long id) {
        repository.deleteById(id);
    }
    
    @Override
    public List<Servico> listarComPendentes() {
        logger.info("Listando serviços com unidades pendentes em OSs não encerradas");
        List<Servico> servicos = repository.findAll();
        return servicos.stream()
                .filter(servico -> servico.getUnidadesUsadasEmOS() != null && servico.getUnidadesUsadasEmOS() > 0)
                .collect(Collectors.toList());
    }
    
    @Override
    public void atualizarUnidadesUsadas() {
        logger.info("Atualizando unidades usadas em OSs para todos os serviços");
        
        List<Servico> todosServicos = repository.findAll();
        List<OrdemServico> osNaoEncerradas = ordemServicoRepository.findByStatusNot("Encerrada");
        
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
    
    private void validarNomeDuplicado(String nome, Long idExcluir) {
        logger.info("Validando nome duplicado para serviço: " + nome + " (excluindo ID: " + idExcluir + ")");
        
        if (nome == null || nome.trim().isEmpty()) {
            logger.warn("Nome do serviço é nulo ou vazio");
            return;
        }
        
        String nomeLimpo = nome.trim();
        logger.info("Nome limpo para validação: " + nomeLimpo);
        
        boolean exists;
        if (idExcluir != null) {
            exists = repository.existsByNomeIgnoreCaseAndIdNot(nomeLimpo, idExcluir);
            logger.info("Verificação para atualização - Existe outro serviço com nome " + nomeLimpo + " (excluindo ID " + idExcluir + "): " + exists);
        } else {
            exists = repository.existsByNomeIgnoreCase(nomeLimpo);
            logger.info("Verificação para criação - Existe serviço com nome " + nomeLimpo + ": " + exists);
        }
        
        if (exists) {
            String mensagem = "Nome do serviço já está cadastrado";
            logger.error(mensagem + ": " + nomeLimpo);
            throw new NomeDuplicadoException(mensagem);
        }
        
        logger.info("Validação de nome concluída com sucesso para serviço: " + nomeLimpo);
    }
}