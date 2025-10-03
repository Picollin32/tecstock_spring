package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tecstock_spring.model.ServicoOrdemServico;

import java.util.List;

@Repository
public interface ServicoOrdemServicoRepository extends JpaRepository<ServicoOrdemServico, Long> {
    
    List<ServicoOrdemServico> findByNumeroOSOrderByDataRealizacaoDesc(String numeroOS);
    
    List<ServicoOrdemServico> findAllByOrderByDataRealizacaoDesc();
    
    List<ServicoOrdemServico> findByServicoIdOrderByDataRealizacaoDesc(Long servicoId);
    
    boolean existsByNumeroOS(String numeroOS);
}
