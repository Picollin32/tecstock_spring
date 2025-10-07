package tecstock_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tecstock_spring.model.AuditoriaLog;
import tecstock_spring.repository.AuditoriaLogRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuditoriaService {
    
    @Autowired
    private AuditoriaLogRepository auditoriaLogRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    public Page<AuditoriaLog> buscarTodosLogs(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return auditoriaLogRepository.findAll(pageable);
    }
    
    public Page<AuditoriaLog> buscarLogsPorUsuario(String usuario, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return auditoriaLogRepository.findByUsuarioOrderByDataHoraDesc(usuario, pageable);
    }
    
    public Page<AuditoriaLog> buscarLogsPorEntidade(String entidade, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return auditoriaLogRepository.findByEntidadeOrderByDataHoraDesc(entidade, pageable);
    }
 
    public List<AuditoriaLog> buscarHistoricoEntidade(String entidade, Long entidadeId) {
        return auditoriaLogRepository.findByEntidadeAndEntidadeIdOrderByDataHoraDesc(entidade, entidadeId);
    }

    public Page<AuditoriaLog> buscarLogsPorOperacao(String operacao, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return auditoriaLogRepository.findByOperacaoOrderByDataHoraDesc(operacao, pageable);
    }
    
    public Page<AuditoriaLog> buscarLogsPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return auditoriaLogRepository.findByPeriodo(dataInicio, dataFim, pageable);
    }
    
    public Page<AuditoriaLog> buscarLogsComFiltros(String usuario, String entidade, String operacao,
                                                   Long entidadeId, LocalDateTime dataInicio, LocalDateTime dataFim,
                                                   int page, int size, String sortBy, String sortDir) {

        String sortColumn = sortBy;
        if ("dataHora".equals(sortBy)) {
            sortColumn = "data_hora";
        } else if ("entidadeId".equals(sortBy)) {
            sortColumn = "entidade_id";
        }
        
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortColumn).ascending() : Sort.by(sortColumn).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return auditoriaLogRepository.findComFiltros(usuario, entidade, operacao, entidadeId, dataInicio, dataFim, pageable);
    }

    public Map<String, Object> gerarRelatorioUsuario(String usuario) {
        Map<String, Object> relatorio = new HashMap<>();
        relatorio.put("usuario", usuario);
        relatorio.put("totalOperacoes", auditoriaLogRepository.countByUsuario(usuario));

        Page<AuditoriaLog> logs = auditoriaLogRepository.findByUsuarioOrderByDataHoraDesc(usuario, PageRequest.of(0, 100));
        
        Map<String, Long> operacoesPorTipo = logs.getContent().stream()
            .collect(Collectors.groupingBy(AuditoriaLog::getOperacao, Collectors.counting()));
        
        Map<String, Long> operacoesPorEntidade = logs.getContent().stream()
            .collect(Collectors.groupingBy(AuditoriaLog::getEntidade, Collectors.counting()));
        
        relatorio.put("operacoesPorTipo", operacoesPorTipo);
        relatorio.put("operacoesPorEntidade", operacoesPorEntidade);
        relatorio.put("ultimasOperacoes", logs.getContent().stream().limit(20).collect(Collectors.toList()));
        
        return relatorio;
    }

    public Map<String, Object> gerarRelatorioGeral() {
        Map<String, Object> relatorio = new HashMap<>();
        
        List<AuditoriaLog> todosLogs = auditoriaLogRepository.findAll();
        
        relatorio.put("totalOperacoes", todosLogs.size());
        
        Map<String, Long> operacoesPorTipo = todosLogs.stream()
            .collect(Collectors.groupingBy(AuditoriaLog::getOperacao, Collectors.counting()));
        
        Map<String, Long> operacoesPorEntidade = todosLogs.stream()
            .collect(Collectors.groupingBy(AuditoriaLog::getEntidade, Collectors.counting()));
        
        Map<String, Long> operacoesPorUsuario = todosLogs.stream()
            .collect(Collectors.groupingBy(AuditoriaLog::getUsuario, Collectors.counting()));
        

        List<AuditoriaLog> ultimasOperacoes = todosLogs.stream()
            .sorted(Comparator.comparing(AuditoriaLog::getDataHora).reversed())
            .limit(50)
            .collect(Collectors.toList());
        
        relatorio.put("operacoesPorTipo", operacoesPorTipo);
        relatorio.put("operacoesPorEntidade", operacoesPorEntidade);
        relatorio.put("operacoesPorUsuario", operacoesPorUsuario);
        relatorio.put("ultimasOperacoes", ultimasOperacoes);
        
        return relatorio;
    }

    public List<AuditoriaLog> buscarAtividadesRecentes() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime ontem = agora.minusHours(24);
        
        Page<AuditoriaLog> logs = auditoriaLogRepository.findByPeriodo(ontem, agora, PageRequest.of(0, 100));
        return logs.getContent();
    }

    public List<String> listarEntidadesAuditadas() {
        return auditoriaLogRepository.findAll().stream()
            .map(AuditoriaLog::getEntidade)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    public List<String> listarUsuariosAtivos() {
        return auditoriaLogRepository.findAll().stream()
            .map(AuditoriaLog::getUsuario)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
}
