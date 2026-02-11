package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tecstock_spring.model.MovimentacaoEstoque;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {
    
    List<MovimentacaoEstoque> findByEmpresaId(Long empresaId);
    Optional<MovimentacaoEstoque> findByIdAndEmpresaId(Long id, Long empresaId);
    
    @Query("SELECT m FROM MovimentacaoEstoque m WHERE m.codigoPeca = ?1 AND m.empresa.id = ?2 ORDER BY COALESCE(m.dataSaida, m.dataEntrada) DESC")
    List<MovimentacaoEstoque> findByCodigoPecaAndEmpresaIdOrderByDataEntradaDesc(String codigoPeca, Long empresaId);
    
    @Query("SELECT m FROM MovimentacaoEstoque m WHERE m.fornecedor.id = ?1 AND m.empresa.id = ?2 ORDER BY COALESCE(m.dataSaida, m.dataEntrada) DESC")
    List<MovimentacaoEstoque> findByFornecedorIdAndEmpresaIdOrderByDataEntradaDesc(Long fornecedorId, Long empresaId);
    
    @Query("SELECT m FROM MovimentacaoEstoque m WHERE m.codigoPeca = ?1 AND m.fornecedor.id = ?2 AND m.empresa.id = ?3 ORDER BY COALESCE(m.dataSaida, m.dataEntrada) DESC")
    List<MovimentacaoEstoque> findByCodigoPecaAndFornecedorIdAndEmpresaIdOrderByDataEntradaDesc(String codigoPeca, Long fornecedorId, Long empresaId);
    
    @Query("SELECT m FROM MovimentacaoEstoque m WHERE m.empresa.id = ?1 ORDER BY COALESCE(m.dataSaida, m.dataEntrada) DESC")
    List<MovimentacaoEstoque> findAllByEmpresaIdOrderByDataEntradaDesc(Long empresaId);
    
    boolean existsByNumeroNotaFiscalAndEmpresaId(String numeroNotaFiscal, Long empresaId);
    
    boolean existsByNumeroNotaFiscalAndFornecedorIdAndEmpresaId(String numeroNotaFiscal, Long fornecedorId, Long empresaId);
    
    @Query("SELECT COUNT(m) > 0 FROM MovimentacaoEstoque m WHERE m.numeroNotaFiscal = ?1 AND m.fornecedor.id = ?2 AND DATE(COALESCE(m.dataSaida, m.dataEntrada)) != ?3 AND m.empresa.id = ?4")
    boolean existsByNumeroNotaFiscalAndFornecedorIdAndDataEntradaNotAndEmpresaId(String numeroNotaFiscal, Long fornecedorId, java.time.LocalDate dataAtual, Long empresaId);
    
    @Query("SELECT m FROM MovimentacaoEstoque m WHERE m.observacoes LIKE %?1% ORDER BY COALESCE(m.dataSaida, m.dataEntrada) DESC")
    List<MovimentacaoEstoque> findByObservacoesContainingOrderByDataEntradaDesc(String observacoes);
}
