package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tecstock_spring.model.Conta;

import java.time.LocalDate;
import java.util.List;

public interface ContaRepository extends JpaRepository<Conta, Long> {

    List<Conta> findByEmpresaIdOrderByAnoReferenciaDescMesReferenciaDesc(Long empresaId);

    List<Conta> findByEmpresaIdAndMesReferenciaAndAnoReferenciaOrderByDataVencimentoAsc(
            Long empresaId, Integer mes, Integer ano);

    List<Conta> findByEmpresaIdAndTipoAndMesReferenciaAndAnoReferenciaOrderByDataVencimentoAsc(
            Long empresaId, String tipo, Integer mes, Integer ano);

    @Query("SELECT c FROM Conta c WHERE c.empresa.id = :empresaId AND c.pago = false AND c.dataVencimento < :hoje")
    List<Conta> findContasAtrasadas(@Param("empresaId") Long empresaId, @Param("hoje") LocalDate hoje);

    @Query("SELECT c FROM Conta c WHERE c.empresa.id = :empresaId AND c.fiadoGrupoId = :grupoId AND c.pago = false" +
           " AND (c.anoReferencia > :ano OR (c.anoReferencia = :ano AND c.mesReferencia > :mes))")
    List<Conta> findFiadoEntradasFuturas(
            @Param("empresaId") Long empresaId,
            @Param("grupoId") String grupoId,
            @Param("mes") Integer mes,
            @Param("ano") Integer ano);

    List<Conta> findByEmpresaIdAndOrdemServicoId(Long empresaId, Long ordemServicoId);

    List<Conta> findByEmpresaIdAndFiadoGrupoId(Long empresaId, String fiadoGrupoId);

    @Query("SELECT c FROM Conta c WHERE c.empresa.id = :empresaId AND c.descricao LIKE CONCAT('Compra NF ', :numeroNota, ' %') AND c.tipo = 'A_PAGAR'")
    List<Conta> findContasCompraByNumeroNota(@Param("empresaId") Long empresaId, @Param("numeroNota") String numeroNota);
}
