package tecstock_spring.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tecstock_spring.dto.GarantiaPaginaDTO;
import tecstock_spring.dto.GarantiaResumoDTO;
import tecstock_spring.dto.GarantiaResumoTotalDTO;
import tecstock_spring.model.Empresa;
import tecstock_spring.model.GarantiaRetorno;
import tecstock_spring.model.OrdemServico;
import tecstock_spring.model.Servico;
import tecstock_spring.repository.EmpresaRepository;
import tecstock_spring.repository.GarantiaRetornoRepository;
import tecstock_spring.repository.OrdemServicoRepository;
import tecstock_spring.repository.ServicoRepository;
import tecstock_spring.util.TenantContext;

@Service
@RequiredArgsConstructor
public class GarantiaServiceImpl implements GarantiaService {

    private static final String STATUS_RECLAMADA = "Reclamada";
    private static final String STATUS_ENCERRADA = "Encerrada";

    private final OrdemServicoRepository ordemServicoRepository;
    private final GarantiaRetornoRepository garantiaRetornoRepository;
    private final ServicoRepository servicoRepository;
    private final EmpresaRepository empresaRepository;

    @Override
    public GarantiaPaginaDTO buscarGarantias(String query, String field, String status, int page, int size) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa nao encontrada no contexto do usuario");
        }

        String queryValue = query == null ? "" : query.trim();
        String fieldValue = normalizarCampoBusca(field);
        List<OrdemServico> ordens = ordemServicoRepository.findGarantiasByQuery(empresaId, queryValue, fieldValue);

        if (ordens.isEmpty()) {
            return GarantiaPaginaDTO.builder()
                    .content(Collections.emptyList())
                    .totalElements(0)
                    .totalPages(0)
                    .number(0)
                    .resumo(GarantiaResumoTotalDTO.builder().total(0).ativas(0).reclamadas(0).expiradas(0).build())
                    .build();
        }

        List<Long> ordemIds = ordens.stream().map(OrdemServico::getId).filter(id -> id != null).collect(Collectors.toList());
        Map<Long, GarantiaRetorno> retornoPorOS = carregarUltimosRetornos(ordemIds, empresaId);

        LocalDate hoje = LocalDate.now();
        List<GarantiaResumoDTO> garantias = new ArrayList<>();

        for (OrdemServico os : ordens) {
            if (!Boolean.TRUE.equals(os.getGarantiaLancada())) {
                continue;
            }
            LocalDate inicioGarantia = os.getDataHoraLancamentoGarantia().toLocalDate();
            int garantiaMeses = os.getGarantiaMeses() != null ? os.getGarantiaMeses() : 0;
            LocalDate fimGarantia = inicioGarantia.plusDays(garantiaMeses * 30L);
            GarantiaRetorno retorno = retornoPorOS.get(os.getId());
            String statusGarantia = resolverStatusGarantia(os, fimGarantia, hoje, retorno != null);

            GarantiaResumoDTO.GarantiaResumoDTOBuilder builder = GarantiaResumoDTO.builder()
                    .id(os.getId())
                    .numeroOS(os.getNumeroOS())
                    .clienteNome(os.getClienteNome())
                    .veiculoNome(os.getVeiculoNome())
                    .veiculoPlaca(os.getVeiculoPlaca())
                    .dataEncerramento(os.getDataHoraLancamentoGarantia())
                    .dataInicioGarantia(inicioGarantia)
                    .dataFimGarantia(fimGarantia)
                    .garantiaMeses(os.getGarantiaMeses())
                    .statusOS(os.getStatus())
                    .statusGarantia(statusGarantia)
                    .mecanicoNome(os.getMecanico() != null ? os.getMecanico().getNome() : null)
                    .consultorNome(os.getConsultor() != null ? os.getConsultor().getNome() : null)
                    .servicos(mapearServicos(os.getServicosRealizados()));

            if (retorno != null) {
                builder.retornoId(retorno.getId());
                builder.retornoServicoId(retorno.getServico().getId());
                builder.retornoServicoNome(retorno.getServico().getNome());
                builder.retornoMotivo(retorno.getMotivo());
            }

            garantias.add(builder.build());
        }

        garantias.sort((a, b) -> b.getDataEncerramento().compareTo(a.getDataEncerramento()));

        GarantiaResumoTotalDTO resumo = calcularResumo(garantias);
        List<GarantiaResumoDTO> filtradas = filtrarPorStatus(garantias, status);

        int totalElements = filtradas.size();
        int totalPages = size <= 0 ? 1 : (int) Math.ceil(totalElements / (double) size);
        int fromIndex = Math.max(0, page * size);
        int toIndex = Math.min(totalElements, fromIndex + size);

        List<GarantiaResumoDTO> pageContent = fromIndex >= totalElements
                ? Collections.emptyList()
                : filtradas.subList(fromIndex, toIndex);

        return GarantiaPaginaDTO.builder()
                .content(pageContent)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .number(page)
                .resumo(resumo)
                .build();
    }

    private String normalizarCampoBusca(String field) {
        if (field == null || field.isBlank()) {
            return "TODOS";
        }

        String normalized = field.trim().toUpperCase();
        return switch (normalized) {
            case "OS", "CLIENTE", "VEICULO", "PLACA", "SERVICO", "MECANICO", "CONSULTOR", "MOTIVO", "TODOS" -> normalized;
            default -> "TODOS";
        };
    }

    @SuppressWarnings("null")
    @Override
    @Transactional
    public GarantiaResumoDTO registrarRetorno(Long ordemServicoId, Long servicoId, String motivo) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa nao encontrada no contexto do usuario");
        }

        if (servicoId == null || motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Servico e motivo sao obrigatorios");
        }

        OrdemServico os = ordemServicoRepository.findByIdAndEmpresaId(ordemServicoId, empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Ordem de servico nao encontrada"));

        if (!Boolean.TRUE.equals(os.getGarantiaLancada())) {
            throw new IllegalArgumentException("A OS nao possui garantia lancada");
        }

        boolean servicoValido = os.getServicosRealizados() != null
                && os.getServicosRealizados().stream().anyMatch(s -> s.getId() != null && s.getId().equals(servicoId));
        if (!servicoValido) {
            throw new IllegalArgumentException("Servico nao pertence a OS informada");
        }

        Servico servico = servicoRepository.findById(servicoId)
                .orElseThrow(() -> new IllegalArgumentException("Servico nao encontrado"));

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalStateException("Empresa nao encontrada"));

        GarantiaRetorno retorno = GarantiaRetorno.builder()
                .empresa(empresa)
                .ordemServico(os)
                .servico(servico)
                .motivo(motivo.trim())
                .build();
        garantiaRetornoRepository.save(retorno);

        if (STATUS_ENCERRADA.equalsIgnoreCase(os.getStatus())) {
            os.setStatus(STATUS_RECLAMADA);
            ordemServicoRepository.save(os);
        }

        LocalDate inicioGarantia = os.getDataHoraLancamentoGarantia().toLocalDate();
        int garantiaMeses = os.getGarantiaMeses() != null ? os.getGarantiaMeses() : 0;
        LocalDate fimGarantia = inicioGarantia.plusDays(garantiaMeses * 30L);
        String statusGarantia = resolverStatusGarantia(os, fimGarantia, LocalDate.now(), true);

        return GarantiaResumoDTO.builder()
                .id(os.getId())
                .numeroOS(os.getNumeroOS())
                .clienteNome(os.getClienteNome())
                .veiculoNome(os.getVeiculoNome())
                .veiculoPlaca(os.getVeiculoPlaca())
                .dataEncerramento(os.getDataHoraLancamentoGarantia())
                .dataInicioGarantia(inicioGarantia)
                .dataFimGarantia(fimGarantia)
                .garantiaMeses(os.getGarantiaMeses())
                .statusOS(os.getStatus())
                .statusGarantia(statusGarantia)
                .mecanicoNome(os.getMecanico() != null ? os.getMecanico().getNome() : null)
                .consultorNome(os.getConsultor() != null ? os.getConsultor().getNome() : null)
                .servicos(mapearServicos(os.getServicosRealizados()))
                .retornoId(retorno.getId())
                .retornoServicoId(servico.getId())
                .retornoServicoNome(servico.getNome())
                .retornoMotivo(retorno.getMotivo())
                .build();
    }

    @Override
    @Transactional
    public GarantiaResumoDTO editarRetorno(Long retornoId, String motivo) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa nao encontrada no contexto do usuario");
        }
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Motivo nao pode ser vazio");
        }

        GarantiaRetorno retorno = garantiaRetornoRepository.findById(Objects.requireNonNull(retornoId))
                .filter(r -> r.getEmpresa().getId().equals(empresaId))
                .orElseThrow(() -> new IllegalArgumentException("Retorno nao encontrado"));

        retorno.setMotivo(motivo.trim());
        garantiaRetornoRepository.save(retorno);

        OrdemServico os = retorno.getOrdemServico();
        LocalDate inicioGarantia = os.getDataHoraLancamentoGarantia().toLocalDate();
        int garantiaMeses = os.getGarantiaMeses() != null ? os.getGarantiaMeses() : 0;
        LocalDate fimGarantia = inicioGarantia.plusDays(garantiaMeses * 30L);
        String statusGarantia = resolverStatusGarantia(os, fimGarantia, LocalDate.now(), true);

        return GarantiaResumoDTO.builder()
                .id(os.getId())
                .numeroOS(os.getNumeroOS())
                .clienteNome(os.getClienteNome())
                .veiculoNome(os.getVeiculoNome())
                .veiculoPlaca(os.getVeiculoPlaca())
                .dataEncerramento(os.getDataHoraLancamentoGarantia())
                .dataInicioGarantia(inicioGarantia)
                .dataFimGarantia(fimGarantia)
                .garantiaMeses(os.getGarantiaMeses())
                .statusOS(os.getStatus())
                .statusGarantia(statusGarantia)
                .mecanicoNome(os.getMecanico() != null ? os.getMecanico().getNome() : null)
                .consultorNome(os.getConsultor() != null ? os.getConsultor().getNome() : null)
                .servicos(mapearServicos(os.getServicosRealizados()))
                .retornoId(retorno.getId())
                .retornoServicoId(retorno.getServico().getId())
                .retornoServicoNome(retorno.getServico().getNome())
                .retornoMotivo(retorno.getMotivo())
                .build();
    }

    @Override
    @Transactional
    public void deletarRetorno(Long retornoId) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa nao encontrada no contexto do usuario");
        }

        GarantiaRetorno retorno = garantiaRetornoRepository.findById(Objects.requireNonNull(retornoId))
                .filter(r -> r.getEmpresa().getId().equals(empresaId))
                .orElseThrow(() -> new IllegalArgumentException("Retorno nao encontrado"));

        OrdemServico os = retorno.getOrdemServico();
        garantiaRetornoRepository.delete(retorno);

        List<GarantiaRetorno> restantes = garantiaRetornoRepository
                .findByOrdemServicoIdInAndEmpresaIdOrderByCreatedAtDesc(List.of(os.getId()), empresaId);
        if (restantes.isEmpty() && STATUS_RECLAMADA.equalsIgnoreCase(os.getStatus())) {
            os.setStatus(STATUS_ENCERRADA);
            ordemServicoRepository.save(os);
        }
    }

    private Map<Long, GarantiaRetorno> carregarUltimosRetornos(List<Long> ordemIds, Long empresaId) {
        if (ordemIds == null || ordemIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<GarantiaRetorno> retornos = garantiaRetornoRepository
                .findByOrdemServicoIdInAndEmpresaIdOrderByCreatedAtDesc(ordemIds, empresaId);
        Map<Long, GarantiaRetorno> retornoPorOS = new HashMap<>();
        for (GarantiaRetorno retorno : retornos) {
            Long osId = retorno.getOrdemServico().getId();
            if (!retornoPorOS.containsKey(osId)) {
                retornoPorOS.put(osId, retorno);
            }
        }
        return retornoPorOS;
    }

    private List<GarantiaResumoDTO.GarantiaServicoDTO> mapearServicos(List<Servico> servicos) {
        if (servicos == null || servicos.isEmpty()) {
            return Collections.emptyList();
        }
        return servicos.stream()
                .filter(s -> s.getId() != null)
                .map(s -> GarantiaResumoDTO.GarantiaServicoDTO.builder()
                        .id(s.getId())
                        .nome(s.getNome())
                        .build())
                .collect(Collectors.toList());
    }

    private String resolverStatusGarantia(OrdemServico os, LocalDate fimGarantia, LocalDate hoje, boolean hasRetorno) {
        if (hasRetorno || STATUS_RECLAMADA.equalsIgnoreCase(os.getStatus())) {
            return "Reclamada";
        }
        return fimGarantia.isBefore(hoje) ? "Expirada" : "Ativa";
    }

    private GarantiaResumoTotalDTO calcularResumo(List<GarantiaResumoDTO> garantias) {
        int total = garantias.size();
        int ativas = 0;
        int reclamadas = 0;
        int expiradas = 0;

        for (GarantiaResumoDTO garantia : garantias) {
            String status = garantia.getStatusGarantia();
            if ("Reclamada".equalsIgnoreCase(status)) {
                reclamadas++;
            } else if ("Expirada".equalsIgnoreCase(status)) {
                expiradas++;
            } else {
                ativas++;
            }
        }

        return GarantiaResumoTotalDTO.builder()
                .total(total)
                .ativas(ativas)
                .reclamadas(reclamadas)
                .expiradas(expiradas)
                .build();
    }

    private List<GarantiaResumoDTO> filtrarPorStatus(List<GarantiaResumoDTO> garantias, String status) {
        if (status == null || status.trim().isEmpty() || "TODOS".equalsIgnoreCase(status)) {
            return garantias;
        }
        String statusFiltro = status.trim().toLowerCase();
        return garantias.stream()
                .filter(g -> g.getStatusGarantia() != null && g.getStatusGarantia().toLowerCase().equals(statusFiltro))
                .collect(Collectors.toList());
    }
}
