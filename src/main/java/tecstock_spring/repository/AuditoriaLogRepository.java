package tecstock_spring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tecstock_spring.model.AuditoriaLog;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaLogRepository extends JpaRepository<AuditoriaLog, Long> {

    Page<AuditoriaLog> findByEmpresaIdOrderByDataHoraDesc(Long empresaId, Pageable pageable);

    Page<AuditoriaLog> findByUsuarioAndEmpresaIdOrderByDataHoraDesc(String usuario, Long empresaId, Pageable pageable);

    Page<AuditoriaLog> findByEntidadeAndEmpresaIdOrderByDataHoraDesc(String entidade, Long empresaId, Pageable pageable);

    List<AuditoriaLog> findByEntidadeAndEntidadeIdAndEmpresaIdOrderByDataHoraDesc(String entidade, Long entidadeId, Long empresaId);

    Page<AuditoriaLog> findByOperacaoAndEmpresaIdOrderByDataHoraDesc(String operacao, Long empresaId, Pageable pageable);

    @Query("SELECT a FROM AuditoriaLog a WHERE a.dataHora BETWEEN :dataInicio AND :dataFim AND a.empresa.id = :empresaId ORDER BY a.dataHora DESC")
    Page<AuditoriaLog> findByPeriodo(@Param("dataInicio") LocalDateTime dataInicio, 
                                     @Param("dataFim") LocalDateTime dataFim, 
                                     @Param("empresaId") Long empresaId,
                                     Pageable pageable);

    @Query(value = "SELECT * FROM auditoria_log a WHERE " +
           "(CAST(:usuario AS TEXT) IS NULL OR :usuario = '' OR a.usuario = :usuario) AND " +
           "(CAST(:entidade AS TEXT) IS NULL OR :entidade = '' OR " +
           "  CASE WHEN POSITION(',' IN :entidade) > 0 THEN " +
           "    a.entidade = ANY(STRING_TO_ARRAY(:entidade, ',')) " +
           "  ELSE " +
           "    a.entidade = :entidade " +
           "  END) AND " +
           "(CAST(:operacao AS TEXT) IS NULL OR :operacao = '' OR a.operacao = :operacao) AND " +
           "(CAST(:entidadeId AS BIGINT) IS NULL OR a.entidade_id = CAST(:entidadeId AS BIGINT)) AND " +
           "(CAST(:dataInicio AS TIMESTAMP) IS NULL OR a.data_hora >= CAST(:dataInicio AS TIMESTAMP)) AND " +
           "(CAST(:dataFim AS TIMESTAMP) IS NULL OR a.data_hora <= CAST(:dataFim AS TIMESTAMP)) AND " +
            "a.empresa_id = CAST(:empresaId AS BIGINT)",
           nativeQuery = true)
    Page<AuditoriaLog> findComFiltros(@Param("usuario") String usuario,
                                      @Param("entidade") String entidade,
                                      @Param("operacao") String operacao,
                                      @Param("entidadeId") Long entidadeId,
                                      @Param("dataInicio") LocalDateTime dataInicio,
                                      @Param("dataFim") LocalDateTime dataFim,
                                      @Param("empresaId") Long empresaId,
                                      Pageable pageable);
    
    Long countByUsuario(String usuario);

    Long countByEntidade(String entidade);

}
