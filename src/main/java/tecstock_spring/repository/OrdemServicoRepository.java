package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tecstock_spring.model.OrdemServico;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {
    
    Optional<OrdemServico> findByNumeroOS(String numeroOS);
    
    boolean existsByNumeroOS(String numeroOS);
    
    List<OrdemServico> findByClienteCpfOrderByDataHoraDesc(String clienteCpf);
    java.util.Optional<OrdemServico> findFirstByClienteCpfOrderByDataHoraDesc(String clienteCpf);
    
    List<OrdemServico> findByVeiculoPlacaOrderByDataHoraDesc(String veiculoPlaca);
    java.util.Optional<OrdemServico> findFirstByVeiculoPlacaOrderByDataHoraDesc(String veiculoPlaca);
    boolean existsByClienteCpf(String clienteCpf);
    boolean existsByVeiculoPlaca(String veiculoPlaca);
    boolean existsByMecanicoId(Long mecanicoId);
    boolean existsByConsultorId(Long consultorId);
    java.util.Optional<OrdemServico> findFirstByMecanicoIdOrderByDataHoraDesc(Long mecanicoId);
    java.util.Optional<OrdemServico> findFirstByConsultorIdOrderByDataHoraDesc(Long consultorId);
    
    List<OrdemServico> findByStatusOrderByDataHoraDesc(String status);
    
    List<OrdemServico> findByChecklistIdOrderByDataHoraDesc(Long checklistId);
    
    @Query("SELECT os FROM OrdemServico os WHERE os.dataHora BETWEEN :inicio AND :fim ORDER BY os.dataHora DESC")
    List<OrdemServico> findByDataHoraBetween(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
    
    List<OrdemServico> findByPrecoTotalGreaterThanEqualOrderByPrecoTotalDesc(Double precoMinimo);
    
    List<OrdemServico> findAllByOrderByCreatedAtDesc();
}
