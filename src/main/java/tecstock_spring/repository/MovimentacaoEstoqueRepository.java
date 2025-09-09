package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tecstock_spring.model.MovimentacaoEstoque;
import java.util.List;

@Repository
public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {
    
    List<MovimentacaoEstoque> findByCodigoPecaOrderByDataMovimentacaoDesc(String codigoPeca);
    
    List<MovimentacaoEstoque> findByFornecedorIdOrderByDataMovimentacaoDesc(Long fornecedorId);
    
    List<MovimentacaoEstoque> findByCodigoPecaAndFornecedorIdOrderByDataMovimentacaoDesc(String codigoPeca, Long fornecedorId);
    
    List<MovimentacaoEstoque> findAllByOrderByDataMovimentacaoDesc();
    
    boolean existsByNumeroNotaFiscal(String numeroNotaFiscal);
}
