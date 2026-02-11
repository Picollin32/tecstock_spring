package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tecstock_spring.model.Orcamento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {
    
    Optional<Orcamento> findByNumeroOrcamento(String numeroOrcamento);
    
    List<Orcamento> findAllByOrderByCreatedAtDesc();
    
    @org.springframework.data.jpa.repository.Query("SELECT o FROM Orcamento o ORDER BY CAST(o.numeroOrcamento AS int) ASC")
    List<Orcamento> findAllOrderByNumeroOrcamentoAsc();
    
    List<Orcamento> findByClienteCpfAndEmpresaIdOrderByDataHoraDesc(String clienteCpf, Long empresaId);
    java.util.Optional<Orcamento> findFirstByClienteCpfAndEmpresaIdOrderByDataHoraDesc(String clienteCpf, Long empresaId);
    
    List<Orcamento> findByVeiculoPlacaAndEmpresaIdOrderByDataHoraDesc(String veiculoPlaca, Long empresaId);
    java.util.Optional<Orcamento> findFirstByVeiculoPlacaAndEmpresaIdOrderByDataHoraDesc(String veiculoPlaca, Long empresaId);
    
    List<Orcamento> findByDataHoraBetweenAndEmpresaId(LocalDateTime inicio, LocalDateTime fim, Long empresaId);
    boolean existsByClienteCpfAndEmpresaId(String clienteCpf, Long empresaId);
    boolean existsByVeiculoPlacaAndEmpresaId(String veiculoPlaca, Long empresaId);
    boolean existsByMecanicoIdAndEmpresaId(Long mecanicoId, Long empresaId);
    boolean existsByConsultorIdAndEmpresaId(Long consultorId, Long empresaId);
    java.util.Optional<Orcamento> findFirstByMecanicoIdAndEmpresaIdOrderByDataHoraDesc(Long mecanicoId, Long empresaId);
    java.util.Optional<Orcamento> findFirstByConsultorIdAndEmpresaIdOrderByDataHoraDesc(Long consultorId, Long empresaId);
    List<Orcamento> findByEmpresaIdOrderByCreatedAtDesc(Long empresaId);
    List<Orcamento> findByEmpresaId(Long empresaId);
    Optional<Orcamento> findByIdAndEmpresaId(Long id, Long empresaId);
    Optional<Orcamento> findByNumeroOrcamentoAndEmpresaId(String numeroOrcamento, Long empresaId);
}