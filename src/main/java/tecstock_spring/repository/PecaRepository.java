package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tecstock_spring.model.Peca;

public interface PecaRepository extends JpaRepository<Peca, Long> {

}