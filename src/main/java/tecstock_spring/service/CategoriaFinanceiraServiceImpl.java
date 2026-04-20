package tecstock_spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tecstock_spring.model.CategoriaFinanceira;
import tecstock_spring.model.Empresa;
import tecstock_spring.repository.CategoriaFinanceiraRepository;
import tecstock_spring.repository.ContaRepository;
import tecstock_spring.repository.EmpresaRepository;
import tecstock_spring.util.TenantContext;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoriaFinanceiraServiceImpl implements CategoriaFinanceiraService {

    private final CategoriaFinanceiraRepository categoriaRepository;
    private final ContaRepository contaRepository;
    private final EmpresaRepository empresaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaFinanceira> listarAtivas() {
        Long empresaId = requireEmpresaId();
        return categoriaRepository.findByEmpresaIdAndAtivoTrueOrderByNomeAsc(empresaId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoriaFinanceira> buscarPaginado(String query, Pageable pageable) {
        Long empresaId = requireEmpresaId();
        String queryNormalizada = query == null ? "" : query.trim();
        return categoriaRepository.findByEmpresaIdAndAtivoTrueAndNomeStartingWithIgnoreCase(empresaId, queryNormalizada, pageable);
    }

    @Override
    @Transactional
    public CategoriaFinanceira criar(CategoriaFinanceira categoria) {
        Long empresaId = requireEmpresaId();
        @SuppressWarnings("null")
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        String nomeNormalizado = normalizarNome(categoria.getNome());
        if (categoriaRepository.existsByEmpresaIdAndNomeIgnoreCase(empresaId, nomeNormalizado)) {
            throw new IllegalArgumentException("Já existe uma categoria com esse nome.");
        }

        categoria.setEmpresa(empresa);
        categoria.setNome(nomeNormalizado);
        categoria.setDescricao(categoria.getDescricao() != null ? categoria.getDescricao().trim() : null);
        if (categoria.getAtivo() == null) {
            categoria.setAtivo(true);
        }
        return categoriaRepository.save(categoria);
    }

    @Override
    @Transactional
    public CategoriaFinanceira atualizar(Long id, CategoriaFinanceira dados) {
        Long empresaId = requireEmpresaId();
        CategoriaFinanceira existente = categoriaRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada."));

        String nomeNormalizado = normalizarNome(dados.getNome());
        if (categoriaRepository.existsByEmpresaIdAndNomeIgnoreCaseAndIdNot(empresaId, nomeNormalizado, id)) {
            throw new IllegalArgumentException("Já existe uma categoria com esse nome.");
        }

        existente.setNome(nomeNormalizado);
        existente.setDescricao(dados.getDescricao() != null ? dados.getDescricao().trim() : null);
        if (dados.getAtivo() != null) {
            existente.setAtivo(dados.getAtivo());
        }
        return categoriaRepository.save(existente);
    }

    @SuppressWarnings("null")
    @Override
    @Transactional
    public void deletar(Long id) {
        Long empresaId = requireEmpresaId();
        CategoriaFinanceira existente = categoriaRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada."));

        if (contaRepository.existsByEmpresaIdAndCategoriaFinanceiraId(empresaId, id)) {
            throw new IllegalArgumentException("Não é possível excluir: categoria vinculada a contas.");
        }

        categoriaRepository.delete(existente);
    }

    private Long requireEmpresaId() {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        return Objects.requireNonNull(empresaId);
    }

    private String normalizarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da categoria é obrigatório.");
        }
        return nome.trim();
    }
}
