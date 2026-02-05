package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tecstock_spring.model.Checklist;
import java.util.List;
import java.util.Optional;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {
    List<Checklist> findByEmpresaId(Long empresaId);
    Optional<Checklist> findByIdAndEmpresaId(Long id, Long empresaId);
    boolean existsByVeiculoPlaca(String veiculoPlaca);
    boolean existsByClienteCpf(String clienteCpf);
    boolean existsByConsultorId(Long consultorId);
    java.util.Optional<Checklist> findFirstByClienteCpfOrderByCreatedAtDesc(String clienteCpf);
    java.util.Optional<Checklist> findFirstByVeiculoPlacaOrderByCreatedAtDesc(String veiculoPlaca);
    java.util.Optional<Checklist> findFirstByConsultorIdOrderByCreatedAtDesc(Long consultorId);
}
