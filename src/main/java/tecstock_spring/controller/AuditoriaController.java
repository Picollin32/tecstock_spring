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

    @GetMapping("/usuario/{usuario}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditoriaLog>> buscarLogsPorUsuario(
            @PathVariable String usuario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<AuditoriaLog> logs = auditoriaService.buscarLogsPorUsuario(usuario, page, size);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/entidade/{entidade}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditoriaLog>> buscarLogsPorEntidade(
            @PathVariable String entidade,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<AuditoriaLog> logs = auditoriaService.buscarLogsPorEntidade(entidade, page, size);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/historico/{entidade}/{entidadeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditoriaLog>> buscarHistoricoEntidade(
            @PathVariable String entidade,
            @PathVariable Long entidadeId) {
        List<AuditoriaLog> historico = auditoriaService.buscarHistoricoEntidade(entidade, entidadeId);
        return ResponseEntity.ok(historico);
    }

    @GetMapping("/operacao/{operacao}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditoriaLog>> buscarLogsPorOperacao(
            @PathVariable String operacao,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<AuditoriaLog> logs = auditoriaService.buscarLogsPorOperacao(operacao, page, size);
        return ResponseEntity.ok(logs);
    }

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

    @GetMapping("/relatorio/usuario/{usuario}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> gerarRelatorioUsuario(@PathVariable String usuario) {
        Map<String, Object> relatorio = auditoriaService.gerarRelatorioUsuario(usuario);
        return ResponseEntity.ok(relatorio);
    }
    
    @GetMapping("/relatorio/geral")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> gerarRelatorioGeral() {
        Map<String, Object> relatorio = auditoriaService.gerarRelatorioGeral();
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/recentes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditoriaLog>> buscarAtividadesRecentes() {
        List<AuditoriaLog> logs = auditoriaService.buscarAtividadesRecentes();
        return ResponseEntity.ok(logs);
    }
    
    @GetMapping("/entidades")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<String>> listarEntidadesAuditadas() {
        List<String> entidades = auditoriaService.listarEntidadesAuditadas();
        return ResponseEntity.ok(entidades);
    }
    
    @GetMapping("/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<String>> listarUsuariosAtivos() {
        List<String> usuarios = auditoriaService.listarUsuariosAtivos();
        return ResponseEntity.ok(usuarios);
    }
}
