package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tecstock_spring.model.PecaOrdemServico;

@Repository
public interface PecaOrdemServicoRepository extends JpaRepository<PecaOrdemServico, Long> {
}