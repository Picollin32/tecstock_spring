package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tecstock_spring.model.Checklist;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {
    boolean existsByVeiculoPlaca(String veiculoPlaca);
}
