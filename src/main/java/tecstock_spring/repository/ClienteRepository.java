package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tecstock_spring.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

}
