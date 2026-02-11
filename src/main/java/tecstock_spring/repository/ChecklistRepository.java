package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
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
}
