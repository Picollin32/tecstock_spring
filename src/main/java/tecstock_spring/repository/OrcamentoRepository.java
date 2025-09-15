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
    
    List<Orcamento> findByClienteCpfOrderByDataHoraDesc(String clienteCpf);
    
    List<Orcamento> findByVeiculoPlacaOrderByDataHoraDesc(String veiculoPlaca);
    
    List<Orcamento> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);
}