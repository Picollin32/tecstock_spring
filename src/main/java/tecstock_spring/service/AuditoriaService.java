package tecstock_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tecstock_spring.model.AuditoriaLog;
import tecstock_spring.repository.AuditoriaLogRepository;
import tecstock_spring.util.TenantContext;

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
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Empresa não encontrada no contexto do usuário");
        }
        
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return auditoriaLogRepository.findByEmpresaIdOrderByDataHoraDesc(empresaId, pageable);
    }
    
    public Page<AuditoriaLog> buscarLogsPorUsuario(String usuario, int page, int size) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Empresa não encontrada no contexto do usuário");
        }
        
        Pageable pageable = PageRequest.of(page, size);
        return auditoriaLogRepository.findByUsuarioAndEmpresaIdOrderByDataHoraDesc(usuario, empresaId, pageable);
    }
    
    public Page<AuditoriaLog> buscarLogsPorEntidade(String entidade, int page, int size) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Empresa não encontrada no contexto do usuário");
        }
        
        Pageable pageable = PageRequest.of(page, size);
        return auditoriaLogRepository.findByEntidadeAndEmpresaIdOrderByDataHoraDesc(entidade, empresaId, pageable);
    }
 
    public List<AuditoriaLog> buscarHistoricoEntidade(String entidade, Long entidadeId) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Empresa não encontrada no contexto do usuário");
        }
        
        return auditoriaLogRepository.findByEntidadeAndEntidadeIdAndEmpresaIdOrderByDataHoraDesc(entidade, entidadeId, empresaId);
    }

    public Page<AuditoriaLog> buscarLogsPorOperacao(String operacao, int page, int size) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Empresa não encontrada no contexto do usuário");
        }
        
        Pageable pageable = PageRequest.of(page, size);

        return auditoriaLogRepository.findComFiltros(null, null, operacao, null, null, null, empresaId, pageable);
    }
    
    public Page<AuditoriaLog> buscarLogsPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim, int page, int size) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Empresa não encontrada no contexto do usuário");
        }
        
        Pageable pageable = PageRequest.of(page, size);
        return auditoriaLogRepository.findComFiltros(null, null, null, null, dataInicio, dataFim, empresaId, pageable);
    }
    
    public Page<AuditoriaLog> buscarLogsComFiltros(String usuario, String entidade, String operacao,
                                                   Long entidadeId, LocalDateTime dataInicio, LocalDateTime dataFim,
                                                   int page, int size, String sortBy, String sortDir) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Empresa não encontrada no contexto do usuário");
        }

        String sortColumn = sortBy;
        if ("dataHora".equals(sortBy)) {
            sortColumn = "data_hora";
        } else if ("entidadeId".equals(sortBy)) {
            sortColumn = "entidade_id";
        }
        
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortColumn).ascending() : Sort.by(sortColumn).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return auditoriaLogRepository.findComFiltros(usuario, entidade, operacao, entidadeId, dataInicio, dataFim, empresaId, pageable);
    }

    public Map<String, Object> gerarRelatorioUsuario(String usuario) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Empresa não encontrada no contexto do usuário");
        }
        
        Map<String, Object> relatorio = new HashMap<>();
        relatorio.put("usuario", usuario);

        Page<AuditoriaLog> logs = auditoriaLogRepository.findByUsuarioAndEmpresaIdOrderByDataHoraDesc(usuario, empresaId, PageRequest.of(0, 100));
        relatorio.put("totalOperacoes", logs.getTotalElements());
        
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
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Empresa não encontrada no contexto do usuário");
        }
        
        Map<String, Object> relatorio = new HashMap<>();
        
        Page<AuditoriaLog> todosLogs = auditoriaLogRepository.findByEmpresaIdOrderByDataHoraDesc(empresaId, PageRequest.of(0, 10000));
        
        relatorio.put("totalOperacoes", todosLogs.getTotalElements());
        
        Map<String, Long> operacoesPorTipo = todosLogs.getContent().stream()
            .collect(Collectors.groupingBy(AuditoriaLog::getOperacao, Collectors.counting()));
        
        Map<String, Long> operacoesPorEntidade = todosLogs.getContent().stream()
            .collect(Collectors.groupingBy(AuditoriaLog::getEntidade, Collectors.counting()));
        
        Map<String, Long> operacoesPorUsuario = todosLogs.getContent().stream()
            .collect(Collectors.groupingBy(AuditoriaLog::getUsuario, Collectors.counting()));
        

        List<AuditoriaLog> ultimasOperacoes = todosLogs.getContent().stream()
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
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Empresa não encontrada no contexto do usuário");
        }
        
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime ontem = agora.minusHours(24);
        
        Page<AuditoriaLog> logs = auditoriaLogRepository.findComFiltros(null, null, null, null, ontem, agora, empresaId, PageRequest.of(0, 100));
        return logs.getContent();
    }

    public List<String> listarEntidadesAuditadas() {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Empresa não encontrada no contexto do usuário");
        }
        
        Page<AuditoriaLog> logs = auditoriaLogRepository.findByEmpresaIdOrderByDataHoraDesc(empresaId, PageRequest.of(0, 10000));
        return logs.getContent().stream()
            .map(AuditoriaLog::getEntidade)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    public List<String> listarUsuariosAtivos() {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Empresa não encontrada no contexto do usuário");
        }
        
        Page<AuditoriaLog> logs = auditoriaLogRepository.findByEmpresaIdOrderByDataHoraDesc(empresaId, PageRequest.of(0, 10000));
        return logs.getContent().stream()
            .map(AuditoriaLog::getUsuario)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
}
