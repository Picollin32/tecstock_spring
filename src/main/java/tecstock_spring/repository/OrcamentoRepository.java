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
    
    List<Orcamento> findByClienteCpfOrderByDataHoraDesc(String clienteCpf);
    java.util.Optional<Orcamento> findFirstByClienteCpfOrderByDataHoraDesc(String clienteCpf);
    
    List<Orcamento> findByVeiculoPlacaOrderByDataHoraDesc(String veiculoPlaca);
    java.util.Optional<Orcamento> findFirstByVeiculoPlacaOrderByDataHoraDesc(String veiculoPlaca);
    
    List<Orcamento> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);
    boolean existsByClienteCpf(String clienteCpf);
    boolean existsByVeiculoPlaca(String veiculoPlaca);
    boolean existsByMecanicoId(Long mecanicoId);
    boolean existsByConsultorId(Long consultorId);
    java.util.Optional<Orcamento> findFirstByMecanicoIdOrderByDataHoraDesc(Long mecanicoId);
    java.util.Optional<Orcamento> findFirstByConsultorIdOrderByDataHoraDesc(Long consultorId);
}