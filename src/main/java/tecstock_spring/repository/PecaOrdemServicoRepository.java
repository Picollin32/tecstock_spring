package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tecstock_spring.model.PecaOrdemServico;

@Repository
public interface PecaOrdemServicoRepository extends JpaRepository<PecaOrdemServico, Long> {
    
    @Query("SELECT COUNT(pos) > 0 FROM PecaOrdemServico pos WHERE pos.peca.id = :pecaId")
    boolean existsByPecaId(@Param("pecaId") Long pecaId);
}