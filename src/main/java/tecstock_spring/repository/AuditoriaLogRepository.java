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
    
    // Buscar todos os logs ordenados por data (mais recente primeiro)
    Page<AuditoriaLog> findAllByOrderByDataHoraDesc(Pageable pageable);
    
    // Buscar logs por usuário
    Page<AuditoriaLog> findByUsuarioOrderByDataHoraDesc(String usuario, Pageable pageable);
    
    // Buscar logs por entidade
    Page<AuditoriaLog> findByEntidadeOrderByDataHoraDesc(String entidade, Pageable pageable);
    
    // Buscar logs por entidade e ID específico
    List<AuditoriaLog> findByEntidadeAndEntidadeIdOrderByDataHoraDesc(String entidade, Long entidadeId);
    
    // Buscar logs por operação (CREATE, UPDATE, DELETE)
    Page<AuditoriaLog> findByOperacaoOrderByDataHoraDesc(String operacao, Pageable pageable);
    
    // Buscar logs por período
    @Query("SELECT a FROM AuditoriaLog a WHERE a.dataHora BETWEEN :dataInicio AND :dataFim ORDER BY a.dataHora DESC")
    Page<AuditoriaLog> findByPeriodo(@Param("dataInicio") LocalDateTime dataInicio, 
                                     @Param("dataFim") LocalDateTime dataFim, 
                                     Pageable pageable);
    
    // Buscar logs com filtros múltiplos
    @Query(value = "SELECT * FROM auditoria_log a WHERE " +
           "(CAST(:usuario AS TEXT) IS NULL OR :usuario = '' OR a.usuario = :usuario) AND " +
           "(CAST(:entidade AS TEXT) IS NULL OR :entidade = '' OR a.entidade = :entidade) AND " +
           "(CAST(:operacao AS TEXT) IS NULL OR :operacao = '' OR a.operacao = :operacao) AND " +
           "(CAST(:entidadeId AS BIGINT) IS NULL OR a.entidade_id = CAST(:entidadeId AS BIGINT)) AND " +
           "(CAST(:dataInicio AS TIMESTAMP) IS NULL OR a.data_hora >= CAST(:dataInicio AS TIMESTAMP)) AND " +
           "(CAST(:dataFim AS TIMESTAMP) IS NULL OR a.data_hora <= CAST(:dataFim AS TIMESTAMP))",
           nativeQuery = true)
    Page<AuditoriaLog> findComFiltros(@Param("usuario") String usuario,
                                      @Param("entidade") String entidade,
                                      @Param("operacao") String operacao,
                                      @Param("entidadeId") Long entidadeId,
                                      @Param("dataInicio") LocalDateTime dataInicio,
                                      @Param("dataFim") LocalDateTime dataFim,
                                      Pageable pageable);
    
    // Contar logs por usuário
    Long countByUsuario(String usuario);
    
    // Contar logs por entidade
    Long countByEntidade(String entidade);
}
