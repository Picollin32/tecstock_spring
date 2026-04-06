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

        if (fornecedor.getNome() == null || fornecedor.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do fornecedor é obrigatório");
        }

        fornecedor.setNome(fornecedor.getNome().trim());
        fornecedor.setServico(fornecedor.getServico());
        fornecedor.setCnpj(normalizarTextoNulo(fornecedor.getCnpj()));
        fornecedor.setTelefone(normalizarTextoNulo(fornecedor.getTelefone()));
        fornecedor.setEmail(normalizarTextoNulo(fornecedor.getEmail()));
        fornecedor.setRua(normalizarTextoNulo(fornecedor.getRua()));
        fornecedor.setNumeroCasa(normalizarTextoNulo(fornecedor.getNumeroCasa()));
        fornecedor.setComplemento(normalizarTextoNulo(fornecedor.getComplemento()));
        fornecedor.setBairro(normalizarTextoNulo(fornecedor.getBairro()));
        fornecedor.setCidade(normalizarTextoNulo(fornecedor.getCidade()));
        fornecedor.setUf(normalizarTextoNulo(fornecedor.getUf()));
        fornecedor.setCep(normalizarTextoNulo(fornecedor.getCep()));
        fornecedor.setCodigoMunicipio(normalizarTextoNulo(fornecedor.getCodigoMunicipio()));

        validarCamposObrigatorios(fornecedor);
        
        if (fornecedor.getCnpj() != null && repository.existsByCnpjAndEmpresaId(fornecedor.getCnpj(), empresaId)) {
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
        
        if (novoFornecedor.getNome() == null || novoFornecedor.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do fornecedor é obrigatório");
        }

        novoFornecedor.setNome(novoFornecedor.getNome().trim());
        novoFornecedor.setServico(novoFornecedor.getServico());
        novoFornecedor.setCnpj(normalizarTextoNulo(novoFornecedor.getCnpj()));
        novoFornecedor.setTelefone(normalizarTextoNulo(novoFornecedor.getTelefone()));
        novoFornecedor.setEmail(normalizarTextoNulo(novoFornecedor.getEmail()));
        novoFornecedor.setRua(normalizarTextoNulo(novoFornecedor.getRua()));
        novoFornecedor.setNumeroCasa(normalizarTextoNulo(novoFornecedor.getNumeroCasa()));
        novoFornecedor.setComplemento(normalizarTextoNulo(novoFornecedor.getComplemento()));
        novoFornecedor.setBairro(normalizarTextoNulo(novoFornecedor.getBairro()));
        novoFornecedor.setCidade(normalizarTextoNulo(novoFornecedor.getCidade()));
        novoFornecedor.setUf(normalizarTextoNulo(novoFornecedor.getUf()));
        novoFornecedor.setCep(normalizarTextoNulo(novoFornecedor.getCep()));
        novoFornecedor.setCodigoMunicipio(normalizarTextoNulo(novoFornecedor.getCodigoMunicipio()));

        validarCamposObrigatorios(novoFornecedor);

        if (novoFornecedor.getCnpj() != null && repository.existsByCnpjAndIdNotAndEmpresaId(novoFornecedor.getCnpj(), id, empresaId)) {
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
    public Page<tecstock_spring.dto.FornecedorPesquisaDTO> buscarPaginado(String query, Boolean servico, Pageable pageable) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        if (query == null || query.trim().isEmpty()) {
            return repository.findByEmpresaId(empresaId, servico, pageable);
        }
        
        return repository.searchByQueryAndEmpresaId(query.trim(), empresaId, servico, pageable);
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

    private String normalizarTextoNulo(String valor) {
        if (valor == null) return null;
        String limpo = valor.trim();
        return limpo.isEmpty() ? null : limpo;
    }

    private void validarCamposObrigatorios(Fornecedor fornecedor) {
        boolean isServico = Boolean.TRUE.equals(fornecedor.getServico());
        if (isServico) {
            return;
        }

        if (fornecedor.getCnpj() == null) {
            throw new IllegalArgumentException("CNPJ é obrigatório para fornecedor de peças.");
        }
        if (fornecedor.getTelefone() == null) {
            throw new IllegalArgumentException("Telefone é obrigatório para fornecedor de peças.");
        }
        if (fornecedor.getEmail() == null) {
            throw new IllegalArgumentException("E-mail é obrigatório para fornecedor de peças.");
        }
        if (fornecedor.getMargemLucro() == null) {
            throw new IllegalArgumentException("Margem de lucro é obrigatória para fornecedor de peças.");
        }
        if (fornecedor.getRua() == null) {
            throw new IllegalArgumentException("Rua é obrigatória para fornecedor de peças.");
        }
        if (fornecedor.getNumeroCasa() == null) {
            throw new IllegalArgumentException("Número é obrigatório para fornecedor de peças.");
        }
        if (fornecedor.getBairro() == null) {
            throw new IllegalArgumentException("Bairro é obrigatório para fornecedor de peças.");
        }
        if (fornecedor.getCidade() == null) {
            throw new IllegalArgumentException("Cidade é obrigatória para fornecedor de peças.");
        }
    }
}
