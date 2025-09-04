package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tecstock_spring.model.Funcionario;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

}
