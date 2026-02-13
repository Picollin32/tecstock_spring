package tecstock_spring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tecstock_spring.model.Checklist;
import java.util.List;
import java.util.Optional;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {
    List<Checklist> findByEmpresaId(Long empresaId);
    Optional<Checklist> findByIdAndEmpresaId(Long id, Long empresaId);
    boolean existsByVeiculoPlacaAndEmpresaId(String veiculoPlaca, Long empresaId);
    boolean existsByClienteCpfAndEmpresaId(String clienteCpf, Long empresaId);
    boolean existsByConsultorIdAndEmpresaId(Long consultorId, Long empresaId);
    java.util.Optional<Checklist> findFirstByClienteCpfAndEmpresaIdOrderByCreatedAtDesc(String clienteCpf, Long empresaId);
    java.util.Optional<Checklist> findFirstByVeiculoPlacaAndEmpresaIdOrderByCreatedAtDesc(String veiculoPlaca, Long empresaId);
    java.util.Optional<Checklist> findFirstByConsultorIdAndEmpresaIdOrderByCreatedAtDesc(Long consultorId, Long empresaId);

    @Query("SELECT c FROM Checklist c WHERE c.empresa.id = :empresaId AND CAST(c.numeroChecklist AS string) LIKE CONCAT(:query, '%')")
    Page<Checklist> searchByNumeroChecklistAndEmpresaId(@Param("query") String query, @Param("empresaId") Long empresaId, Pageable pageable);

    @Query("SELECT c FROM Checklist c WHERE c.empresa.id = :empresaId AND LOWER(c.veiculoPlaca) LIKE LOWER(CONCAT(:query, '%'))")
    Page<Checklist> searchByVeiculoPlacaAndEmpresaId(@Param("query") String query, @Param("empresaId") Long empresaId, Pageable pageable);
    
        @Query("SELECT c FROM Checklist c WHERE c.empresa.id = :empresaId AND (" +
            "CAST(c.numeroChecklist AS string) LIKE CONCAT(:query, '%') OR " +
            "LOWER(c.veiculoPlaca) LIKE LOWER(CONCAT(:query, '%')))")
    Page<Checklist> searchByQueryAndEmpresaId(@Param("query") String query, @Param("empresaId") Long empresaId, Pageable pageable);
    
    Page<Checklist> findByEmpresaIdOrderByCreatedAtDesc(Long empresaId, Pageable pageable);
    
    @Query("SELECT c FROM Checklist c WHERE c.empresa.id = :empresaId ORDER BY c.createdAt DESC")
    List<Checklist> findTopByEmpresaIdOrderByCreatedAtDesc(@Param("empresaId") Long empresaId, Pageable pageable);
}
