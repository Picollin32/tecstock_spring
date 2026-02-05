package tecstock_spring.service;

import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tecstock_spring.controller.MarcaController;
import tecstock_spring.exception.NomeDuplicadoException;
import tecstock_spring.exception.MarcaEmUsoException;
import tecstock_spring.model.Empresa;
import tecstock_spring.model.Marca;
import tecstock_spring.repository.EmpresaRepository;
import tecstock_spring.repository.MarcaRepository;
import tecstock_spring.util.TenantContext;
import tecstock_spring.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarcaServiceImpl implements MarcaService {

    private final MarcaRepository repository;
    private final EmpresaRepository empresaRepository;
    private final VeiculoRepository veiculoRepository;
    Logger logger = Logger.getLogger(MarcaController.class);

    @Override
    public Marca salvar(Marca marca) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        validarNomeDuplicado(marca.getMarca(), null, empresaId);
        
        Empresa empresa = empresaRepository.findById(empresaId)
            .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        marca.setEmpresa(empresa);
        
        Marca marcaSalva = repository.save(marca);
        logger.info("Marca salva com sucesso na empresa " + empresaId + ": " + marcaSalva);
        return marcaSalva;
    }

    @Override
    public Marca buscarPorId(Long id) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        return repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Marca não encontrada ou não pertence à sua empresa"));
    }

    @Override
    public List<Marca> listarTodos() {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        List<Marca> marcas = repository.findByEmpresaId(empresaId);
        if (marcas != null && marcas.isEmpty()) {
            logger.info("Nenhuma marca cadastrada na empresa " + empresaId);
            System.out.println("Nenhuma marca cadastrada na empresa " + empresaId);
        } else if (marcas != null && !marcas.isEmpty()) {
            logger.info(marcas.size() + " marcas encontradas na empresa " + empresaId);
            System.out.println(marcas.size() + " marcas encontradas na empresa " + empresaId);
        }
        return marcas;
    }

    @Override
    public Marca atualizar(Long id, Marca novoMarca) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        Marca categoriaExistente = repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Marca não encontrada ou não pertence à sua empresa"));
        
        validarNomeDuplicado(novoMarca.getMarca(), id, empresaId);
        BeanUtils.copyProperties(novoMarca, categoriaExistente, "id", "empresa", "createdAt", "updatedAt");
        return repository.save(categoriaExistente);
    }

    @Override
    public void deletar(Long id) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        Marca marca = repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Marca não encontrada ou não pertence à sua empresa"));
        
        if (veiculoRepository.existsByMarca(marca)) {
            throw new MarcaEmUsoException("Marca não pode ser excluída pois está vinculada a um veículo");
        }
        
        repository.deleteById(id);
        logger.info("Marca excluída com sucesso da empresa " + empresaId + ": " + marca.getMarca());
    }
    
    private void validarNomeDuplicado(String nome, Long idExcluir, Long empresaId) {
        logger.info("Validando nome duplicado para marca: " + nome + " (excluindo ID: " + idExcluir + ") na empresa " + empresaId);
        
        if (nome == null || nome.trim().isEmpty()) {
            logger.warn("Nome da marca é nulo ou vazio");
            return;
        }
        
        String nomeLimpo = nome.trim();
        logger.info("Nome limpo para validação: " + nomeLimpo);
        
        boolean exists;
        if (idExcluir != null) {
            exists = repository.existsByNomeIgnoreCaseAndIdNotAndEmpresaId(nomeLimpo, idExcluir, empresaId);
            logger.info("Checando duplicação (exceto ID " + idExcluir + "): " + exists);
        } else {
            exists = repository.existsByNomeIgnoreCaseAndEmpresaId(nomeLimpo, empresaId);
            logger.info("Verificação para criação - Existe marca com nome " + nomeLimpo + ": " + exists);
        }
        
        if (exists) {
            String mensagem = "Nome da marca já está cadastrado";
            logger.error(mensagem + ": " + nomeLimpo);
            throw new NomeDuplicadoException(mensagem);
        }
        
        logger.info("Validação de nome concluída com sucesso para marca: " + nomeLimpo);
    }
}
