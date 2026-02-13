package tecstock_spring.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tecstock_spring.controller.FornecedorController;
import tecstock_spring.exception.FornecedorEmUsoException;
import tecstock_spring.model.Empresa;
import tecstock_spring.model.Fornecedor;
import tecstock_spring.repository.EmpresaRepository;
import tecstock_spring.repository.FornecedorRepository;
import tecstock_spring.repository.PecaRepository;
import tecstock_spring.util.TenantContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FornecedorServiceImpl implements FornecedorService {

    private final FornecedorRepository repository;
    private final EmpresaRepository empresaRepository;
    private final PecaRepository pecaRepository;
    Logger logger = LoggerFactory.getLogger(FornecedorController.class);

    @Override
    public Fornecedor salvar(Fornecedor fornecedor) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        if (repository.existsByCnpjAndEmpresaId(fornecedor.getCnpj(), empresaId)) {
            throw new RuntimeException("CNPJ já cadastrado nesta empresa: " + fornecedor.getCnpj());
        }
        
        Empresa empresa = empresaRepository.findById(empresaId)
            .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        fornecedor.setEmpresa(empresa);
        
        Fornecedor fornecedorSalvo = repository.save(fornecedor);
        logger.info("Fornecedor salvo com sucesso na empresa " + empresaId + ": " + fornecedorSalvo);
        return fornecedorSalvo;
    }

    @Override
    public Fornecedor buscarPorId(Long id) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        return repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado ou não pertence à sua empresa"));
    }

    @Override
    public List<Fornecedor> listarTodos() {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        List<Fornecedor> fornecedores = repository.findByEmpresaId(empresaId);
        if (fornecedores != null && fornecedores.isEmpty()) {
            logger.info("Nenhum fornecedor cadastrado na empresa " + empresaId);
        } else if (fornecedores != null && !fornecedores.isEmpty()) {
            logger.info(fornecedores.size() + " fornecedores encontrados na empresa " + empresaId);
        }
        return fornecedores;
    }

    @Override
    @SuppressWarnings("null")
    public Fornecedor atualizar(Long id, Fornecedor novoFornecedor) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        Fornecedor fornecedorExistente = repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado ou não pertence à sua empresa"));
        
        if (repository.existsByCnpjAndIdNotAndEmpresaId(novoFornecedor.getCnpj(), id, empresaId)) {
            throw new RuntimeException("CNPJ já cadastrado em outro fornecedor desta empresa: " + novoFornecedor.getCnpj());
        }
        
        BeanUtils.copyProperties(novoFornecedor, fornecedorExistente, "id", "empresa", "pecasComDesconto", "createdAt", "updatedAt");
        return repository.save(fornecedorExistente);
    }

    @Override
    @SuppressWarnings("null")
    public void deletar(Long id) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        Fornecedor fornecedor = repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado ou não pertence à sua empresa"));
        
        if (pecaRepository.existsByFornecedor(fornecedor)) {
            throw new FornecedorEmUsoException("Fornecedor não pode ser excluído pois está vinculado a uma peça");
        }
        
        repository.deleteById(id);
        logger.info("Fornecedor excluído com sucesso da empresa " + empresaId + ": " + fornecedor.getNome());
    }
    
    @Override
    public Page<tecstock_spring.dto.FornecedorPesquisaDTO> buscarPaginado(String query, Pageable pageable) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        if (query == null || query.trim().isEmpty()) {
            return repository.findByEmpresaId(empresaId, pageable);
        }
        
        return repository.searchByQueryAndEmpresaId(query.trim(), empresaId, pageable);
    }
    
    @Override
    public List<tecstock_spring.dto.FornecedorPesquisaDTO> listarUltimosParaInicio(int limit) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        Pageable pageable = PageRequest.of(0, limit);
        return repository.findTopByEmpresaIdOrderByCreatedAtDesc(empresaId, pageable);
    }
}
