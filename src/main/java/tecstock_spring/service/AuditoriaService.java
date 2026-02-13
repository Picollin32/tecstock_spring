package tecstock_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.lang.NonNull;
import tecstock_spring.model.AuditoriaLog;
import tecstock_spring.repository.AuditoriaLogRepository;
import tecstock_spring.util.TenantContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuditoriaService {
    
    @Autowired
    private AuditoriaLogRepository auditoriaLogRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    private static final DateTimeFormatter CSV_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public Page<AuditoriaLog> buscarTodosLogs(int page, int size, String sortBy, String sortDir) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Empresa não encontrada no contexto do usuário");
        }
        
        Pageable pageable = PageRequest.of(page, size, buildJpaSort(sortBy, sortDir));
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
        
        Pageable pageable = PageRequest.of(page, size, buildNativeSort(sortBy, sortDir));
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
    
    public String exportarRegistrosParaCSV(String usuario, String entidade, String operacao,
                                           Long entidadeId, LocalDateTime dataInicio, LocalDateTime dataFim,
                                           LocalDate dia, Integer ano, Integer mes) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Empresa não encontrada no contexto do usuário");
        }
        
        LocalDateTime inicio = dataInicio;
        LocalDateTime fim = dataFim;

        if (dia != null) {
            inicio = dia.atStartOfDay();
            fim = dia.plusDays(1).atStartOfDay().minusNanos(1);
        } else if (inicio == null && fim == null && ano != null && mes != null) {
            inicio = LocalDateTime.of(ano, mes, 1, 0, 0, 0);
            fim = inicio.plusMonths(1).minusNanos(1);
        }
        
        Page<AuditoriaLog> logs = auditoriaLogRepository.findComFiltros(
            usuario, entidade, operacao, entidadeId, inicio, fim, empresaId,
            PageRequest.of(0, 50000, buildNativeSort("data_hora", "desc"))
        );
        
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Data/Hora,Usuário,Entidade,ID Entidade,Operação,Valores Antigos,Valores Novos,Descrição\n");
        
        for (AuditoriaLog log : logs.getContent()) {
            csv.append(escapeCsv(String.valueOf(log.getId()))).append(',');
            csv.append(escapeCsv(formatDataHora(log.getDataHora()))).append(',');
            csv.append(escapeCsv(log.getUsuario())).append(',');
            csv.append(escapeCsv(log.getEntidade())).append(',');
            csv.append(escapeCsv(String.valueOf(log.getEntidadeId()))).append(',');
            csv.append(escapeCsv(log.getOperacao())).append(',');
            csv.append(escapeCsv(defaultString(log.getValoresAntigos()))).append(',');
            csv.append(escapeCsv(defaultString(log.getValoresNovos()))).append(',');
            csv.append(escapeCsv(defaultString(log.getDescricao()))).append("\n");
        }
        
        return csv.toString();
    }
    
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String cleaned = value.replace("\r", " ").replace("\n", " ").trim();
        String escaped = cleaned.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\"")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }

    @NonNull
    private Sort buildNativeSort(String sortBy, String sortDir) {
        String normalized = normalizeNativeSortField(sortBy);
        Sort.Order order = sortDir != null && sortDir.equalsIgnoreCase("asc")
                ? Sort.Order.asc(normalized)
                : Sort.Order.desc(normalized);
        return Sort.by(order);
    }

    @NonNull
    private Sort buildJpaSort(String sortBy, String sortDir) {
        String normalized = normalizeJpaSortField(sortBy);
        Sort.Order order = sortDir != null && sortDir.equalsIgnoreCase("asc")
                ? Sort.Order.asc(normalized)
                : Sort.Order.desc(normalized);
        return Sort.by(order);
    }

    @NonNull
    private String normalizeNativeSortField(String sortBy) {
        String candidate = Objects.requireNonNullElse(sortBy, "data_hora");
        return switch (candidate) {
            case "data_hora", "dataHora" -> "data_hora";
            case "usuario" -> "usuario";
            case "entidade" -> "entidade";
            case "operacao" -> "operacao";
            case "entidadeId", "entidade_id" -> "entidade_id";
            default -> "data_hora";
        };
    }

    @NonNull
    private String formatDataHora(LocalDateTime dataHora) {
        if (dataHora == null) {
            return "";
        }
        return Objects.requireNonNull(CSV_DATE_FORMAT.format(dataHora), "formatted date cannot be null");
    }

    @NonNull
    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    @NonNull
    private String normalizeJpaSortField(String sortBy) {
        String candidate = Objects.requireNonNullElse(sortBy, "dataHora");
        return switch (candidate) {
            case "data_hora" -> "dataHora";
            case "entidade_id" -> "entidadeId";
            default -> candidate;
        };
    }
}
