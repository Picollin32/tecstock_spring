package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tecstock_spring.model.TipoPagamento;
import java.util.List;
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
    
    @Query("SELECT COALESCE(MAX(t.id), 0) FROM TipoPagamento t")
    Long findMaxId();
    List<TipoPagamento> findByEmpresaId(Long empresaId);
    Optional<TipoPagamento> findByIdAndEmpresaId(Long id, Long empresaId);
    Optional<TipoPagamento> findByNomeAndEmpresaId(String nome, Long empresaId);
    Optional<TipoPagamento> findByCodigoAndEmpresaId(Integer codigo, Long empresaId);
    boolean existsByNomeAndEmpresaId(String nome, Long empresaId);
    boolean existsByCodigoAndEmpresaId(Integer codigo, Long empresaId);
    boolean existsByNomeAndIdNotAndEmpresaId(String nome, Long id, Long empresaId);
    boolean existsByCodigoAndIdNotAndEmpresaId(Integer codigo, Long id, Long empresaId);
}
