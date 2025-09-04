package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tecstock_spring.model.Veiculo;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

}
