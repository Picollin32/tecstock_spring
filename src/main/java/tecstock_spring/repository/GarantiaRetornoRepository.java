package tecstock_spring.repository;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tecstock_spring.model.GarantiaRetorno;

public interface GarantiaRetornoRepository extends JpaRepository<GarantiaRetorno, Long> {

    @Query("SELECT gr FROM GarantiaRetorno gr WHERE gr.empresa.id = :empresaId AND gr.ordemServico.id IN :ordemIds ORDER BY gr.createdAt DESC")
    List<GarantiaRetorno> findByOrdemServicoIdInAndEmpresaIdOrderByCreatedAtDesc(
            @Param("ordemIds") List<Long> ordemIds,
            @Param("empresaId") Long empresaId);

    @Query("SELECT DISTINCT gr.ordemServico.id FROM GarantiaRetorno gr WHERE gr.empresa.id = :empresaId")
    Set<Long> findOrdemServicoIdsWithRetornoByEmpresaId(@Param("empresaId") Long empresaId);
}
