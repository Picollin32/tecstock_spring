package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tecstock_spring.model.Servico;

public interface ServicoRepository extends JpaRepository<Servico, Long> {

}