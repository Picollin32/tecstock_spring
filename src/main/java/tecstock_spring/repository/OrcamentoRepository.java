package tecstock_spring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tecstock_spring.model.Orcamento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {
    
    Optional<Orcamento> findByNumeroOrcamento(String numeroOrcamento);
    
    List<Orcamento> findAllByOrderByCreatedAtDesc();
    
    @org.springframework.data.jpa.repository.Query("SELECT o FROM Orcamento o ORDER BY CAST(o.numeroOrcamento AS int) ASC")
    List<Orcamento> findAllOrderByNumeroOrcamentoAsc();
    
    List<Orcamento> findByClienteCpfAndEmpresaIdOrderByDataHoraDesc(String clienteCpf, Long empresaId);
    java.util.Optional<Orcamento> findFirstByClienteCpfAndEmpresaIdOrderByDataHoraDesc(String clienteCpf, Long empresaId);
    
    List<Orcamento> findByVeiculoPlacaAndEmpresaIdOrderByDataHoraDesc(String veiculoPlaca, Long empresaId);
    java.util.Optional<Orcamento> findFirstByVeiculoPlacaAndEmpresaIdOrderByDataHoraDesc(String veiculoPlaca, Long empresaId);
    
    List<Orcamento> findByDataHoraBetweenAndEmpresaId(LocalDateTime inicio, LocalDateTime fim, Long empresaId);
    boolean existsByClienteCpfAndEmpresaId(String clienteCpf, Long empresaId);
    boolean existsByVeiculoPlacaAndEmpresaId(String veiculoPlaca, Long empresaId);
    boolean existsByMecanicoIdAndEmpresaId(Long mecanicoId, Long empresaId);
    boolean existsByConsultorIdAndEmpresaId(Long consultorId, Long empresaId);
    java.util.Optional<Orcamento> findFirstByMecanicoIdAndEmpresaIdOrderByDataHoraDesc(Long mecanicoId, Long empresaId);
    java.util.Optional<Orcamento> findFirstByConsultorIdAndEmpresaIdOrderByDataHoraDesc(Long consultorId, Long empresaId);
    List<Orcamento> findByEmpresaIdOrderByCreatedAtDesc(Long empresaId);
    List<Orcamento> findByEmpresaId(Long empresaId);
    Optional<Orcamento> findByIdAndEmpresaId(Long id, Long empresaId);
    Optional<Orcamento> findByNumeroOrcamentoAndEmpresaId(String numeroOrcamento, Long empresaId);

    @Query("SELECT o FROM Orcamento o WHERE o.empresa.id = :empresaId AND o.numeroOrcamento LIKE CONCAT(:query, '%')")
    Page<Orcamento> searchByNumeroOrcamentoAndEmpresaId(@Param("query") String query, @Param("empresaId") Long empresaId, Pageable pageable);

    @Query("SELECT o FROM Orcamento o WHERE o.empresa.id = :empresaId AND LOWER(o.clienteNome) LIKE LOWER(CONCAT(:query, '%'))")
    Page<Orcamento> searchByClienteNomeAndEmpresaId(@Param("query") String query, @Param("empresaId") Long empresaId, Pageable pageable);

    @Query("SELECT o FROM Orcamento o WHERE o.empresa.id = :empresaId AND LOWER(o.veiculoPlaca) LIKE LOWER(CONCAT(:query, '%'))")
    Page<Orcamento> searchByVeiculoPlacaAndEmpresaId(@Param("query") String query, @Param("empresaId") Long empresaId, Pageable pageable);
    
    @Query("SELECT o FROM Orcamento o WHERE o.empresa.id = :empresaId AND (" +
           "o.numeroOrcamento LIKE CONCAT(:query, '%') OR " +
           "LOWER(o.clienteNome) LIKE LOWER(CONCAT(:query, '%')) OR " +
           "LOWER(o.veiculoPlaca) LIKE LOWER(CONCAT(:query, '%')))")
    Page<Orcamento> searchByQueryAndEmpresaId(@Param("query") String query, @Param("empresaId") Long empresaId, Pageable pageable);
    
    Page<Orcamento> findByEmpresaIdOrderByCreatedAtDesc(Long empresaId, Pageable pageable);
    
    @Query("SELECT COUNT(o) > 0 FROM Orcamento o JOIN o.pecasOrcadas po WHERE po.peca.id = :pecaId")
    boolean existsByPecaId(@Param("pecaId") Long pecaId);
}