package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tecstock_spring.model.Agendamento;
import java.util.List;
import java.util.Optional;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    boolean existsByPlacaVeiculo(String placaVeiculo);

    List<Agendamento> findByEmpresaId(Long empresaId);
    Optional<Agendamento> findByIdAndEmpresaId(Long id, Long empresaId);
}