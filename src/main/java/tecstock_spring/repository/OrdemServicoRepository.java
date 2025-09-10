package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tecstock_spring.model.OrdemServico;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {
    
    // Buscar por número da OS
    Optional<OrdemServico> findByNumeroOS(String numeroOS);
    
    // Verificar se já existe OS com o número
    boolean existsByNumeroOS(String numeroOS);
    
    // Buscar OSs por cliente (CPF)
    List<OrdemServico> findByClienteCpfOrderByDataHoraDesc(String clienteCpf);
    
    // Buscar OSs por placa do veículo
    List<OrdemServico> findByVeiculoPlacaOrderByDataHoraDesc(String veiculoPlaca);
    
    // Buscar OSs por status
    List<OrdemServico> findByStatusOrderByDataHoraDesc(String status);
    
    // Buscar OSs por checklist vinculado
    List<OrdemServico> findByChecklistIdOrderByDataHoraDesc(Long checklistId);
    
    // Buscar OSs em um período específico
    @Query("SELECT os FROM OrdemServico os WHERE os.dataHora BETWEEN :inicio AND :fim ORDER BY os.dataHora DESC")
    List<OrdemServico> findByDataHoraBetween(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
    
    // Buscar OSs com preço total acima de um valor
    List<OrdemServico> findByPrecoTotalGreaterThanEqualOrderByPrecoTotalDesc(Double precoMinimo);
    
    // Listar todas ordenadas por data de criação (mais recente primeiro)
    List<OrdemServico> findAllByOrderByCreatedAtDesc();
}
