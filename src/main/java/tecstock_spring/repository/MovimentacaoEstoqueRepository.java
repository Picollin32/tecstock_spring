package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tecstock_spring.model.MovimentacaoEstoque;
import java.util.List;

@Repository
public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {
    
    @Query("SELECT m FROM MovimentacaoEstoque m WHERE m.codigoPeca = ?1 ORDER BY COALESCE(m.dataSaida, m.dataEntrada) DESC")
    List<MovimentacaoEstoque> findByCodigoPecaOrderByDataEntradaDesc(String codigoPeca);
    
    @Query("SELECT m FROM MovimentacaoEstoque m WHERE m.fornecedor.id = ?1 ORDER BY COALESCE(m.dataSaida, m.dataEntrada) DESC")
    List<MovimentacaoEstoque> findByFornecedorIdOrderByDataEntradaDesc(Long fornecedorId);
    
    @Query("SELECT m FROM MovimentacaoEstoque m WHERE m.codigoPeca = ?1 AND m.fornecedor.id = ?2 ORDER BY COALESCE(m.dataSaida, m.dataEntrada) DESC")
    List<MovimentacaoEstoque> findByCodigoPecaAndFornecedorIdOrderByDataEntradaDesc(String codigoPeca, Long fornecedorId);
    
    @Query("SELECT m FROM MovimentacaoEstoque m ORDER BY COALESCE(m.dataSaida, m.dataEntrada) DESC")
    List<MovimentacaoEstoque> findAllByOrderByDataEntradaDesc();
    
    boolean existsByNumeroNotaFiscal(String numeroNotaFiscal);
    
    boolean existsByNumeroNotaFiscalAndFornecedorId(String numeroNotaFiscal, Long fornecedorId);
    
    @Query("SELECT m FROM MovimentacaoEstoque m WHERE m.observacoes LIKE %?1% ORDER BY COALESCE(m.dataSaida, m.dataEntrada) DESC")
    List<MovimentacaoEstoque> findByObservacoesContainingOrderByDataEntradaDesc(String observacoes);
}
