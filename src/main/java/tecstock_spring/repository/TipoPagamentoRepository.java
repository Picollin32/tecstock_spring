package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tecstock_spring.model.TipoPagamento;
import java.util.Optional;

public interface TipoPagamentoRepository extends JpaRepository<TipoPagamento, Long> {
    Optional<TipoPagamento> findByNome(String nome);
    Optional<TipoPagamento> findByCodigo(Integer codigo);
    boolean existsByNome(String nome);
    boolean existsByCodigo(Integer codigo);
    boolean existsByNomeAndIdNot(String nome, Long id);
    boolean existsByCodigoAndIdNot(Integer codigo, Long id);
    
    @Query("SELECT COALESCE(MAX(t.codigo), 0) FROM TipoPagamento t")
    Integer findMaxCodigo();
}
