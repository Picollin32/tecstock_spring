package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tecstock_spring.model.Fabricante;

@Repository
public interface FabricanteRepository extends JpaRepository<Fabricante, Long> {
}