package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tecstock_spring.dto.TipoPagamentoPesquisaDTO;
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
    
    @Query("SELECT new tecstock_spring.dto.TipoPagamentoPesquisaDTO(t.id, t.nome, t.codigo, t.createdAt, t.updatedAt) FROM TipoPagamento t WHERE t.empresa.id = :empresaId AND LOWER(t.nome) LIKE LOWER(CONCAT(:query, '%'))")
    org.springframework.data.domain.Page<TipoPagamentoPesquisaDTO> searchByQueryAndEmpresaId(@org.springframework.data.repository.query.Param("query") String query, @org.springframework.data.repository.query.Param("empresaId") Long empresaId, org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT new tecstock_spring.dto.TipoPagamentoPesquisaDTO(t.id, t.nome, t.codigo, t.createdAt, t.updatedAt) FROM TipoPagamento t WHERE t.empresa.id = :empresaId")
    org.springframework.data.domain.Page<TipoPagamentoPesquisaDTO> findByEmpresaId(Long empresaId, org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT new tecstock_spring.dto.TipoPagamentoPesquisaDTO(t.id, t.nome, t.codigo, t.createdAt, t.updatedAt) FROM TipoPagamento t WHERE t.empresa.id = :empresaId ORDER BY t.createdAt DESC")
    List<TipoPagamentoPesquisaDTO> findTopByEmpresaIdOrderByCreatedAtDesc(@org.springframework.data.repository.query.Param("empresaId") Long empresaId, org.springframework.data.domain.Pageable pageable);
}
