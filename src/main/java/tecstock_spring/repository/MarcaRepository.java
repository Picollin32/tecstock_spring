package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tecstock_spring.model.Marca;

public interface MarcaRepository extends JpaRepository<Marca, Long> {

}
