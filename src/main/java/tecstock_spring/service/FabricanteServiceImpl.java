package tecstock_spring.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tecstock_spring.controller.FabricanteController;
import tecstock_spring.exception.NomeDuplicadoException;
import tecstock_spring.exception.FabricanteEmUsoException;
import tecstock_spring.model.Empresa;
import tecstock_spring.model.Fabricante;
import tecstock_spring.repository.EmpresaRepository;
import tecstock_spring.repository.FabricanteRepository;
import tecstock_spring.util.TenantContext;
import tecstock_spring.repository.PecaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FabricanteServiceImpl implements FabricanteService {

    private final FabricanteRepository repository;
    private final EmpresaRepository empresaRepository;
    private final PecaRepository pecaRepository;
    Logger logger = LoggerFactory.getLogger(FabricanteController.class);

    @Override
    public Fabricante salvar(Fabricante fabricante) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        validarNomeDuplicado(fabricante.getNome(), null, empresaId);
        
        Empresa empresa = empresaRepository.findById(empresaId)
            .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        fabricante.setEmpresa(empresa);
        
        Fabricante fabricanteSalvo = repository.save(fabricante);
        logger.info("Fabricante salvo com sucesso na empresa " + empresaId + ": " + fabricanteSalvo);
        return fabricanteSalvo;
    }

    @Override
    public Fabricante buscarPorId(Long id) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        return repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Fabricante não encontrado ou não pertence à sua empresa"));
    }

    @Override
    public List<Fabricante> listarTodos() {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        List<Fabricante> fabricantes = repository.findByEmpresaId(empresaId);
        if (fabricantes.isEmpty()) {
            logger.info("Nenhum fabricante cadastrado na empresa " + empresaId);
        } else {
            logger.info(fabricantes.size() + " fabricantes encontrados na empresa " + empresaId);
        }
        return fabricantes;
    }

    @Override
    @SuppressWarnings("null")
    public Fabricante atualizar(Long id, Fabricante novoFabricante) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        Fabricante fabricanteExistente = repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Fabricante não encontrado ou não pertence à sua empresa"));
        
        validarNomeDuplicado(novoFabricante.getNome(), id, empresaId);
        BeanUtils.copyProperties(novoFabricante, fabricanteExistente, "id", "empresa", "createdAt", "updatedAt");
        return repository.save(fabricanteExistente);
    }

    @Override
    @SuppressWarnings("null")
    public void deletar(Long id) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        Fabricante fabricante = repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Fabricante não encontrado ou não pertence à sua empresa"));
        
        if (pecaRepository.existsByFabricante(fabricante)) {
            throw new FabricanteEmUsoException("Fabricante não pode ser excluído pois está vinculado a uma peça");
        }
        
        repository.deleteById(id);
        logger.info("Fabricante excluído com sucesso da empresa " + empresaId + ": " + fabricante.getNome());
    }
    
    private void validarNomeDuplicado(String nome, Long idExcluir, Long empresaId) {
        logger.info("Validando nome duplicado para fabricante: " + nome + " (excluindo ID: " + idExcluir + ") na empresa " + empresaId);
        
        if (nome == null || nome.trim().isEmpty()) {
            logger.warn("Nome do fabricante é nulo ou vazio");
            return;
        }
        
        String nomeLimpo = nome.trim();
        logger.info("Nome limpo para validação: " + nomeLimpo);
        
        boolean exists;
        if (idExcluir != null) {
            exists = repository.existsByNomeIgnoreCaseAndIdNotAndEmpresaId(nomeLimpo, idExcluir, empresaId);
            logger.info("Verificação para atualização - Existe outro fabricante com nome " + nomeLimpo + " (excluindo ID " + idExcluir + ") na empresa " + empresaId + ": " + exists);
        } else {
            exists = repository.existsByNomeIgnoreCaseAndEmpresaId(nomeLimpo, empresaId);
            logger.info("Verificação para criação - Existe fabricante com nome " + nomeLimpo + " na empresa " + empresaId + ": " + exists);
        }
        
        if (exists) {
            String mensagem = "Nome do fabricante já está cadastrado nesta empresa";
            logger.error(mensagem + ": " + nomeLimpo);
            throw new NomeDuplicadoException(mensagem);
        }
        
        logger.info("Validação de nome concluída com sucesso para fabricante: " + nomeLimpo);
    }
}
