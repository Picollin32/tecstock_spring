package tecstock_spring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    List<OrdemServico> findByClienteCpfAndEmpresaIdOrderByDataHoraDesc(String clienteCpf, Long empresaId);
    java.util.Optional<OrdemServico> findFirstByClienteCpfAndEmpresaIdOrderByDataHoraDesc(String clienteCpf, Long empresaId);
    
    List<OrdemServico> findByVeiculoPlacaAndEmpresaIdOrderByDataHoraDesc(String veiculoPlaca, Long empresaId);
    java.util.Optional<OrdemServico> findFirstByVeiculoPlacaAndEmpresaIdOrderByDataHoraDesc(String veiculoPlaca, Long empresaId);
    boolean existsByClienteCpfAndEmpresaId(String clienteCpf, Long empresaId);
    boolean existsByVeiculoPlacaAndEmpresaId(String veiculoPlaca, Long empresaId);
    boolean existsByMecanicoIdAndEmpresaId(Long mecanicoId, Long empresaId);
    boolean existsByConsultorIdAndEmpresaId(Long consultorId, Long empresaId);
    java.util.Optional<OrdemServico> findFirstByMecanicoIdAndEmpresaIdOrderByDataHoraDesc(Long mecanicoId, Long empresaId);
    java.util.Optional<OrdemServico> findFirstByConsultorIdAndEmpresaIdOrderByDataHoraDesc(Long consultorId, Long empresaId);
    
    List<OrdemServico> findByStatusAndEmpresaIdOrderByDataHoraDesc(String status, Long empresaId);
    
    List<OrdemServico> findByStatusNotAndEmpresaId(String status, Long empresaId);
    
    List<OrdemServico> findByChecklistIdAndEmpresaIdOrderByDataHoraDesc(Long checklistId, Long empresaId);
    
    @Query("SELECT os FROM OrdemServico os WHERE os.dataHora BETWEEN :inicio AND :fim AND os.empresa.id = :empresaId ORDER BY os.dataHora DESC")
    List<OrdemServico> findByDataHoraBetweenAndEmpresaId(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim, @Param("empresaId") Long empresaId);
    
    List<OrdemServico> findByPrecoTotalGreaterThanEqualAndEmpresaIdOrderByPrecoTotalDesc(Double precoMinimo, Long empresaId);
    
    List<OrdemServico> findAllByEmpresaIdOrderByCreatedAtDesc(Long empresaId);
    
    @Query("SELECT os FROM OrdemServico os WHERE os.empresa.id = :empresaId ORDER BY CAST(os.numeroOS AS int) ASC")
    List<OrdemServico> findAllOrderByNumeroOSAsc(@Param("empresaId") Long empresaId);
    
    List<OrdemServico> findByStatusAndPrazoFiadoDiasIsNotNullAndEmpresaIdOrderByDataHoraEncerramentoAsc(String status, Long empresaId);
    List<OrdemServico> findByEmpresaId(Long empresaId);
    Optional<OrdemServico> findByIdAndEmpresaId(Long id, Long empresaId);
    Optional<OrdemServico> findByNumeroOSAndEmpresaId(String numeroOS, Long empresaId);

    @Query("SELECT os FROM OrdemServico os WHERE os.empresa.id = :empresaId AND os.numeroOS LIKE CONCAT(:query, '%')")
    Page<OrdemServico> searchByNumeroOSAndEmpresaId(@Param("query") String query, @Param("empresaId") Long empresaId, Pageable pageable);

    @Query("SELECT os FROM OrdemServico os WHERE os.empresa.id = :empresaId AND LOWER(os.clienteNome) LIKE LOWER(CONCAT(:query, '%'))")
    Page<OrdemServico> searchByClienteNomeAndEmpresaId(@Param("query") String query, @Param("empresaId") Long empresaId, Pageable pageable);

    @Query("SELECT os FROM OrdemServico os WHERE os.empresa.id = :empresaId AND LOWER(os.veiculoPlaca) LIKE LOWER(CONCAT(:query, '%'))")
    Page<OrdemServico> searchByVeiculoPlacaAndEmpresaId(@Param("query") String query, @Param("empresaId") Long empresaId, Pageable pageable);

    Page<OrdemServico> findByEmpresaIdOrderByCreatedAtDesc(Long empresaId, Pageable pageable);
    
    @Query("SELECT os FROM OrdemServico os WHERE os.empresa.id = :empresaId AND (" +
           "os.numeroOS LIKE CONCAT(:query, '%') OR " +
           "LOWER(os.clienteNome) LIKE LOWER(CONCAT(:query, '%')) OR " +
           "LOWER(os.veiculoPlaca) LIKE LOWER(CONCAT(:query, '%')))")
    Page<OrdemServico> searchByQueryAndEmpresaId(@Param("query") String query, @Param("empresaId") Long empresaId, Pageable pageable);

}
