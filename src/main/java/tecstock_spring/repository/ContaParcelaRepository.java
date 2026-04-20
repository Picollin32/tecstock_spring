package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tecstock_spring.model.ContaParcela;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ContaParcelaRepository extends JpaRepository<ContaParcela, Long> {

        boolean existsByConta_Id(Long contaId);

    @Query("SELECT p FROM ContaParcela p JOIN FETCH p.conta c WHERE c.empresa.id = :empresaId AND p.dataVencimento >= :inicio AND p.dataVencimento <= :fim ORDER BY p.dataVencimento ASC, p.parcelaNumero ASC")
    List<ContaParcela> findParcelasDoMes(
            @Param("empresaId") Long empresaId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    @Query("SELECT p FROM ContaParcela p JOIN FETCH p.conta c WHERE c.empresa.id = :empresaId AND c.tipo = :tipo AND p.dataVencimento >= :inicio AND p.dataVencimento <= :fim ORDER BY p.dataVencimento ASC, p.parcelaNumero ASC")
    List<ContaParcela> findParcelasDoMesPorTipo(
            @Param("empresaId") Long empresaId,
            @Param("tipo") String tipo,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    @Query("SELECT p FROM ContaParcela p JOIN FETCH p.conta c WHERE c.empresa.id = :empresaId AND p.pago = false AND p.dataVencimento < :hoje ORDER BY p.dataVencimento ASC, p.parcelaNumero ASC")
    List<ContaParcela> findParcelasAtrasadas(
            @Param("empresaId") Long empresaId,
            @Param("hoje") LocalDate hoje);

    @Query("SELECT DISTINCT p.conta.id FROM ContaParcela p WHERE p.conta.id IN :contaIds")
    List<Long> findContaIdsComParcelas(@Param("contaIds") List<Long> contaIds);

    Optional<ContaParcela> findByIdAndContaEmpresaId(Long id, Long empresaId);

        Optional<ContaParcela> findFirstByConta_IdAndPagoFalseOrderByParcelaNumeroAsc(Long contaId);

        Optional<ContaParcela> findFirstByConta_IdAndPagoTrueOrderByParcelaNumeroDesc(Long contaId);

        long countByConta_IdAndPagoFalse(Long contaId);

        List<ContaParcela> findByConta_IdOrderByParcelaNumeroAsc(Long contaId);
}
