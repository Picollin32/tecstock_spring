package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tecstock_spring.model.Checklist;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {
    boolean existsByVeiculoPlaca(String veiculoPlaca);
    boolean existsByClienteCpf(String clienteCpf);
    boolean existsByConsultorId(Long consultorId);
    java.util.Optional<Checklist> findFirstByClienteCpfOrderByCreatedAtDesc(String clienteCpf);
    java.util.Optional<Checklist> findFirstByVeiculoPlacaOrderByCreatedAtDesc(String veiculoPlaca);
    java.util.Optional<Checklist> findFirstByConsultorIdOrderByCreatedAtDesc(Long consultorId);
}
