package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tecstock_spring.model.Agendamento;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
}