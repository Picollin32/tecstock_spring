package tecstock_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tecstock_spring.model.AuditoriaLog;
import tecstock_spring.service.AuditoriaService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auditoria")
@CrossOrigin(origins = "*")
public class AuditoriaController {
    
    @Autowired
    private AuditoriaService auditoriaService;
    
    /**
     * Buscar todos os logs de auditoria (paginado)
     * Acesso apenas para ADMIN
     * @param sortBy Campo para ordenação: dataHora, usuario, entidade, operacao (padrão: dataHora)
     * @param sortDir Direção: asc ou desc (padrão: desc)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditoriaLog>> buscarTodosLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "dataHora") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Page<AuditoriaLog> logs = auditoriaService.buscarTodosLogs(page, size, sortBy, sortDir);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * Buscar logs por usuário específico
     * Acesso apenas para ADMIN
     */
    @GetMapping("/usuario/{usuario}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditoriaLog>> buscarLogsPorUsuario(
            @PathVariable String usuario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<AuditoriaLog> logs = auditoriaService.buscarLogsPorUsuario(usuario, page, size);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * Buscar logs por tipo de entidade (Cliente, Veiculo, etc)
     * Acesso apenas para ADMIN
     */
    @GetMapping("/entidade/{entidade}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditoriaLog>> buscarLogsPorEntidade(
            @PathVariable String entidade,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<AuditoriaLog> logs = auditoriaService.buscarLogsPorEntidade(entidade, page, size);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * Buscar histórico completo de uma entidade específica
     * Exemplo: GET /api/auditoria/historico/Cliente/123
     * Acesso apenas para ADMIN
     */
    @GetMapping("/historico/{entidade}/{entidadeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditoriaLog>> buscarHistoricoEntidade(
            @PathVariable String entidade,
            @PathVariable Long entidadeId) {
        List<AuditoriaLog> historico = auditoriaService.buscarHistoricoEntidade(entidade, entidadeId);
        return ResponseEntity.ok(historico);
    }
    
    /**
     * Buscar logs por tipo de operação (CREATE, UPDATE, DELETE)
     * Acesso apenas para ADMIN
     */
    @GetMapping("/operacao/{operacao}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditoriaLog>> buscarLogsPorOperacao(
            @PathVariable String operacao,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<AuditoriaLog> logs = auditoriaService.buscarLogsPorOperacao(operacao, page, size);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * Buscar logs por período
     * Formato: yyyy-MM-dd'T'HH:mm:ss
     * Exemplo: ?dataInicio=2024-01-01T00:00:00&dataFim=2024-12-31T23:59:59
     * Acesso apenas para ADMIN
     */
    @GetMapping("/periodo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditoriaLog>> buscarLogsPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<AuditoriaLog> logs = auditoriaService.buscarLogsPorPeriodo(dataInicio, dataFim, page, size);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * Buscar logs com múltiplos filtros
     * Acesso apenas para ADMIN
     * @param sortBy Campo para ordenação: dataHora, usuario, entidade, operacao, entidadeId (padrão: dataHora)
     * @param sortDir Direção: asc ou desc (padrão: desc)
     */
    @GetMapping("/filtros")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditoriaLog>> buscarLogsComFiltros(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String entidade,
            @RequestParam(required = false) String operacao,
            @RequestParam(required = false) Long entidadeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "dataHora") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Page<AuditoriaLog> logs = auditoriaService.buscarLogsComFiltros(
            usuario, entidade, operacao, entidadeId, dataInicio, dataFim, page, size, sortBy, sortDir);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * Gerar relatório de atividades por usuário
     * Acesso apenas para ADMIN
     */
    @GetMapping("/relatorio/usuario/{usuario}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> gerarRelatorioUsuario(@PathVariable String usuario) {
        Map<String, Object> relatorio = auditoriaService.gerarRelatorioUsuario(usuario);
        return ResponseEntity.ok(relatorio);
    }
    
    /**
     * Gerar relatório geral de auditoria
     * Acesso apenas para ADMIN
     */
    @GetMapping("/relatorio/geral")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> gerarRelatorioGeral() {
        Map<String, Object> relatorio = auditoriaService.gerarRelatorioGeral();
        return ResponseEntity.ok(relatorio);
    }
    
    /**
     * Buscar atividades recentes (últimas 24 horas)
     * Acesso apenas para ADMIN
     */
    @GetMapping("/recentes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditoriaLog>> buscarAtividadesRecentes() {
        List<AuditoriaLog> logs = auditoriaService.buscarAtividadesRecentes();
        return ResponseEntity.ok(logs);
    }
    
    /**
     * Listar todas as entidades auditadas
     * Acesso apenas para ADMIN
     */
    @GetMapping("/entidades")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<String>> listarEntidadesAuditadas() {
        List<String> entidades = auditoriaService.listarEntidadesAuditadas();
        return ResponseEntity.ok(entidades);
    }
    
    /**
     * Listar todos os usuários que realizaram operações
     * Acesso apenas para ADMIN
     */
    @GetMapping("/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<String>> listarUsuariosAtivos() {
        List<String> usuarios = auditoriaService.listarUsuariosAtivos();
        return ResponseEntity.ok(usuarios);
    }
}
