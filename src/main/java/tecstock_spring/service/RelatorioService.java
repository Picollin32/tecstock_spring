package tecstock_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tecstock_spring.dto.RelatorioAgendamentosDTO;
import tecstock_spring.dto.RelatorioComissaoDTO;
import tecstock_spring.dto.RelatorioEstoqueDTO;
import tecstock_spring.dto.RelatorioFiadoDTO;
import tecstock_spring.dto.RelatorioFinanceiroDTO;
import tecstock_spring.dto.RelatorioGarantiasDTO;
import tecstock_spring.dto.RelatorioServicosDTO;
import tecstock_spring.model.*;
import tecstock_spring.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    @Autowired
    private PecaRepository pecaRepository;

    @Autowired
    private ServicoOrdemServicoRepository servicoOrdemServicoRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    public RelatorioAgendamentosDTO gerarRelatorioAgendamentos(LocalDate dataInicio, LocalDate dataFim) {

        List<Agendamento> agendamentos = agendamentoRepository.findAll().stream()
                .filter(a -> a.getData() != null &&
                        !a.getData().isBefore(dataInicio) &&
                        !a.getData().isAfter(dataFim))
                .collect(Collectors.toList());

        int totalAgendamentos = agendamentos.size();

        Map<LocalDate, Long> agendamentosPorDiaMap = agendamentos.stream()
                .collect(Collectors.groupingBy(
                        Agendamento::getData,
                        Collectors.counting()
                ));

        List<RelatorioAgendamentosDTO.AgendamentoPorDiaDTO> agendamentosPorDia = agendamentosPorDiaMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new RelatorioAgendamentosDTO.AgendamentoPorDiaDTO(
                        entry.getKey().toString(),
                        entry.getValue().intValue()
                ))
                .collect(Collectors.toList());

        Map<String, Long> agendamentosPorMecanicoMap = agendamentos.stream()
                .filter(a -> a.getNomeMecanico() != null && !a.getNomeMecanico().isEmpty())
                .collect(Collectors.groupingBy(
                        Agendamento::getNomeMecanico,
                        Collectors.counting()
                ));

        List<RelatorioAgendamentosDTO.AgendamentoPorMecanicoDTO> agendamentosPorMecanicoLista = agendamentosPorMecanicoMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(entry -> new RelatorioAgendamentosDTO.AgendamentoPorMecanicoDTO(
                        entry.getKey(),
                        entry.getValue().intValue()
                ))
                .collect(Collectors.toList());

        int mecanicosAtivos = (int) agendamentos.stream()
                .map(Agendamento::getNomeMecanico)
                .filter(nome -> nome != null && !nome.isEmpty())
                .distinct()
                .count();

        return new RelatorioAgendamentosDTO(
                dataInicio,
                dataFim,
                totalAgendamentos,
                mecanicosAtivos,
                agendamentosPorDia,
                agendamentosPorMecanicoLista
        );
    }

    public RelatorioServicosDTO gerarRelatorioServicos(LocalDate dataInicio, LocalDate dataFim) {

        List<OrdemServico> ordens = ordemServicoRepository.findAll().stream()
                .filter(os -> os.getDataHora() != null && 
                        !os.getDataHora().toLocalDate().isBefore(dataInicio) && 
                        !os.getDataHora().toLocalDate().isAfter(dataFim) &&
                        !"Cancelada".equalsIgnoreCase(os.getStatus()))
                .collect(Collectors.toList());

        int totalOrdens = ordens.size();
        int finalizadas = (int) ordens.stream()
                .filter(os -> "Encerrada".equalsIgnoreCase(os.getStatus()))
                .count();
        int emAndamento = (int) ordens.stream()
                .filter(os -> "Aberta".equalsIgnoreCase(os.getStatus()))
                .count();
        int canceladas = 0;

        List<ServicoOrdemServico> servicosRealizados = servicoOrdemServicoRepository.findAll().stream()
                .filter(s -> s.getDataRealizacao() != null &&
                        !s.getDataRealizacao().toLocalDate().isBefore(dataInicio) &&
                        !s.getDataRealizacao().toLocalDate().isAfter(dataFim))
                .collect(Collectors.toList());

        BigDecimal valorTotalServicos = servicosRealizados.stream()
                .map(s -> s.getValor() != null ? BigDecimal.valueOf(s.getValor()) : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal descontoServicos = ordens.stream()
                .filter(os -> "Encerrada".equalsIgnoreCase(os.getStatus()))
                .map(os -> os.getDescontoServicos() != null ? BigDecimal.valueOf(os.getDescontoServicos()) : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorServicosRealizados = valorTotalServicos.subtract(descontoServicos);

        int totalServicos = servicosRealizados.size();

        Map<Long, Integer> contagemPorServico = new HashMap<>();
        Map<Long, BigDecimal> valorPorServico = new HashMap<>();
        
        for (ServicoOrdemServico sos : servicosRealizados) {
            if (sos.getServico() != null) {
                Long idServico = sos.getServico().getId();
                contagemPorServico.put(idServico, contagemPorServico.getOrDefault(idServico, 0) + 1);
                
                BigDecimal valorAtual = valorPorServico.getOrDefault(idServico, BigDecimal.ZERO);
                BigDecimal valorServico = sos.getValor() != null ? BigDecimal.valueOf(sos.getValor()) : BigDecimal.ZERO;
                valorPorServico.put(idServico, valorAtual.add(valorServico));
            }
        }

        List<RelatorioServicosDTO.ItemServicoDTO> servicosMaisRealizados = contagemPorServico.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .map(entry -> {
                    Long idServico = entry.getKey();
                    Integer quantidade = entry.getValue();
                    BigDecimal valorTotal = valorPorServico.getOrDefault(idServico, BigDecimal.ZERO);

                    Optional<ServicoOrdemServico> sosOpt = servicosRealizados.stream()
                            .filter(s -> s.getServico() != null && s.getServico().getId().equals(idServico))
                            .findFirst();
                    
                    if (sosOpt.isPresent()) {
                        String nomeServico = sosOpt.get().getServico().getNome();
                        return new RelatorioServicosDTO.ItemServicoDTO(idServico, nomeServico, quantidade, valorTotal);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        BigDecimal valorMedio = totalOrdens > 0 
                ? valorServicosRealizados.divide(BigDecimal.valueOf(totalOrdens), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        List<OrdemServico> ordensFinalizadas = ordens.stream()
                .filter(os -> "Encerrada".equalsIgnoreCase(os.getStatus()) && 
                        os.getDataHora() != null && 
                        os.getDataHoraEncerramento() != null)
                .collect(Collectors.toList());

        Double tempoMedio = 0.0;
        if (!ordensFinalizadas.isEmpty()) {
            long totalDias = ordensFinalizadas.stream()
                    .mapToLong(os -> ChronoUnit.DAYS.between(os.getDataHora(), os.getDataHoraEncerramento()))
                    .sum();
            tempoMedio = (double) totalDias / ordensFinalizadas.size();
        }

        return new RelatorioServicosDTO(
                dataInicio, dataFim, 
                valorServicosRealizados, totalServicos, servicosMaisRealizados,
                totalOrdens, finalizadas, emAndamento, canceladas,
                descontoServicos, valorMedio, tempoMedio
        );
    }

    public RelatorioEstoqueDTO gerarRelatorioEstoque(LocalDate dataInicio, LocalDate dataFim) {
        List<MovimentacaoEstoque> movimentacoes = movimentacaoEstoqueRepository.findAll().stream()
                .filter(m -> {
                    LocalDateTime data = m.getTipoMovimentacao() == MovimentacaoEstoque.TipoMovimentacao.ENTRADA 
                            ? m.getDataEntrada() : m.getDataSaida();
                    return data != null && 
                            !data.toLocalDate().isBefore(dataInicio) && 
                            !data.toLocalDate().isAfter(dataFim);
                })
                .collect(Collectors.toList());

        int totalMovimentacoes = movimentacoes.size();
        int entradas = (int) movimentacoes.stream()
                .filter(m -> m.getTipoMovimentacao() == MovimentacaoEstoque.TipoMovimentacao.ENTRADA).count();
        int saidas = (int) movimentacoes.stream()
                .filter(m -> m.getTipoMovimentacao() == MovimentacaoEstoque.TipoMovimentacao.SAIDA).count();

        BigDecimal valorEntradas = movimentacoes.stream()
                .filter(m -> m.getTipoMovimentacao() == MovimentacaoEstoque.TipoMovimentacao.ENTRADA)
                .map(m -> {
                    BigDecimal valor = m.getPrecoUnitario() != null ? BigDecimal.valueOf(m.getPrecoUnitario()) : BigDecimal.ZERO;
                    int qtd = m.getQuantidade();
                    return valor.multiply(BigDecimal.valueOf(qtd));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorSaidas = movimentacoes.stream()
                .filter(m -> m.getTipoMovimentacao() == MovimentacaoEstoque.TipoMovimentacao.SAIDA)
                .map(m -> {
                    BigDecimal valor = m.getPrecoFinal() != null ? BigDecimal.valueOf(m.getPrecoFinal()) : BigDecimal.ZERO;
                    int qtd = m.getQuantidade();
                    return valor.multiply(BigDecimal.valueOf(qtd));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Peca> pecas = pecaRepository.findAll();
        BigDecimal valorTotalEstoque = pecas.stream()
                .map(p -> {
                    BigDecimal preco = BigDecimal.valueOf(p.getPrecoUnitario());
                    int qtd = p.getQuantidadeEstoque();
                    return preco.multiply(BigDecimal.valueOf(qtd));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Integer> movimentacoesPorPeca = new HashMap<>();
        for (MovimentacaoEstoque m : movimentacoes) {
            if (m.getCodigoPeca() != null) {
                String codigo = m.getCodigoPeca();
                int qtd = m.getQuantidade();
                movimentacoesPorPeca.put(codigo, movimentacoesPorPeca.getOrDefault(codigo, 0) + qtd);
            }
        }

        List<RelatorioEstoqueDTO.ItemEstoqueDTO> pecasMaisMovimentadas = movimentacoesPorPeca.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .map(entry -> {
                    Optional<Peca> pecaOpt = pecas.stream()
                            .filter(p -> entry.getKey().equals(p.getCodigoFabricante()))
                            .findFirst();
                    if (pecaOpt.isPresent()) {
                        Peca peca = pecaOpt.get();
                        return new RelatorioEstoqueDTO.ItemEstoqueDTO(
                                peca.getId(),
                                peca.getNome(),
                                entry.getValue(),
                                BigDecimal.valueOf(peca.getPrecoUnitario())
                        );
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());


        List<RelatorioEstoqueDTO.ItemEstoqueDTO> pecasEstoqueBaixo = pecas.stream()
                .filter(p -> p.getQuantidadeEstoque() < 10)
                .map(p -> new RelatorioEstoqueDTO.ItemEstoqueDTO(
                        p.getId(),
                        p.getNome(),
                        p.getQuantidadeEstoque(),
                        BigDecimal.valueOf(p.getPrecoUnitario())
                ))
                .collect(Collectors.toList());

        RelatorioEstoqueDTO relatorio = new RelatorioEstoqueDTO();
        relatorio.setDataInicio(dataInicio);
        relatorio.setDataFim(dataFim);
        relatorio.setTotalMovimentacoes(totalMovimentacoes);
        relatorio.setTotalEntradas(entradas);
        relatorio.setTotalSaidas(saidas);
        relatorio.setValorTotalEstoque(valorTotalEstoque);
        relatorio.setValorEntradas(valorEntradas);
        relatorio.setValorSaidas(valorSaidas);
        relatorio.setPecasMaisMovimentadas(pecasMaisMovimentadas);
        relatorio.setPecasEstoqueBaixo(pecasEstoqueBaixo);

        return relatorio;
    }

    public RelatorioFinanceiroDTO gerarRelatorioFinanceiro(LocalDate dataInicio, LocalDate dataFim) {

        List<MovimentacaoEstoque> saidas = movimentacaoEstoqueRepository.findAll().stream()
                .filter(m -> m.getTipoMovimentacao() == MovimentacaoEstoque.TipoMovimentacao.SAIDA &&
                        m.getDataSaida() != null &&
                        !m.getDataSaida().toLocalDate().isBefore(dataInicio) && 
                        !m.getDataSaida().toLocalDate().isAfter(dataFim))
                .collect(Collectors.toList());

        BigDecimal receitaPecas = saidas.stream()
                .map(m -> {
                    BigDecimal valor = m.getPrecoFinal() != null ? BigDecimal.valueOf(m.getPrecoFinal()) : BigDecimal.ZERO;
                    int qtd = m.getQuantidade();
                    return valor.multiply(BigDecimal.valueOf(qtd));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<ServicoOrdemServico> servicosRealizados = servicoOrdemServicoRepository.findAll().stream()
                .filter(s -> s.getDataRealizacao() != null &&
                        !s.getDataRealizacao().toLocalDate().isBefore(dataInicio) &&
                        !s.getDataRealizacao().toLocalDate().isAfter(dataFim))
                .collect(Collectors.toList());

        BigDecimal receitaServicos = servicosRealizados.stream()
                .map(s -> s.getValor() != null ? BigDecimal.valueOf(s.getValor()) : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<MovimentacaoEstoque> entradas = movimentacaoEstoqueRepository.findAll().stream()
                .filter(m -> m.getTipoMovimentacao() == MovimentacaoEstoque.TipoMovimentacao.ENTRADA &&
                        m.getDataEntrada() != null &&
                        !m.getDataEntrada().toLocalDate().isBefore(dataInicio) && 
                        !m.getDataEntrada().toLocalDate().isAfter(dataFim))
                .collect(Collectors.toList());

        BigDecimal despesasEstoque = entradas.stream()
                .map(m -> {
                    BigDecimal valor = m.getPrecoUnitario() != null ? BigDecimal.valueOf(m.getPrecoUnitario()) : BigDecimal.ZERO;
                    int qtd = m.getQuantidade();
                    return valor.multiply(BigDecimal.valueOf(qtd));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<OrdemServico> ordensFinalizadas = ordemServicoRepository.findAll().stream()
                .filter(os -> "Encerrada".equalsIgnoreCase(os.getStatus()) && 
                        os.getDataHoraEncerramento() != null &&
                        !os.getDataHoraEncerramento().toLocalDate().isBefore(dataInicio) && 
                        !os.getDataHoraEncerramento().toLocalDate().isAfter(dataFim))
                .collect(Collectors.toList());

        BigDecimal descontosPecas = ordensFinalizadas.stream()
                .map(os -> os.getDescontoPecas() != null ? BigDecimal.valueOf(os.getDescontoPecas()) : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal descontosServicos = ordensFinalizadas.stream()
                .map(os -> os.getDescontoServicos() != null ? BigDecimal.valueOf(os.getDescontoServicos()) : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal descontosTotal = descontosPecas.add(descontosServicos);
        BigDecimal receitaTotal = receitaPecas.add(receitaServicos);
        BigDecimal lucroEstimado = receitaPecas
                .add(receitaServicos)
                .subtract(despesasEstoque)
                .subtract(descontosPecas)
                .subtract(descontosServicos);

        Map<String, BigDecimal> receitaPorTipoPagamento = new HashMap<>();
        Map<String, Integer> quantidadePorTipoPagamento = new HashMap<>();

        for (OrdemServico os : ordensFinalizadas) {
            if (os.getTipoPagamento() != null) {
                String tipo = os.getTipoPagamento().getNome();
                BigDecimal valor = os.getPrecoTotal() != null ? BigDecimal.valueOf(os.getPrecoTotal()) : BigDecimal.ZERO;
                
                receitaPorTipoPagamento.put(tipo, 
                        receitaPorTipoPagamento.getOrDefault(tipo, BigDecimal.ZERO).add(valor));
                quantidadePorTipoPagamento.put(tipo, 
                        quantidadePorTipoPagamento.getOrDefault(tipo, 0) + 1);
            }
        }

        BigDecimal ticketMedio = !ordensFinalizadas.isEmpty() 
                ? receitaTotal.divide(BigDecimal.valueOf(ordensFinalizadas.size()), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new RelatorioFinanceiroDTO(
                dataInicio, dataFim, receitaTotal, receitaServicos, receitaPecas,
                despesasEstoque, descontosPecas, descontosServicos, descontosTotal,
                lucroEstimado, receitaPorTipoPagamento,
                quantidadePorTipoPagamento, ticketMedio
        );
    }

    public RelatorioComissaoDTO gerarRelatorioComissao(LocalDate dataInicio, LocalDate dataFim, Long mecanicoId) {

        Funcionario mecanico = funcionarioRepository.findById(mecanicoId)
                .orElseThrow(() -> new RuntimeException("Mecânico não encontrado"));
        
        String mecanicoNome = mecanico.getNome();

        List<OrdemServico> ordensEncerradas = ordemServicoRepository.findAll().stream()
                .filter(os -> "Encerrada".equalsIgnoreCase(os.getStatus()) &&
                        os.getDataHoraEncerramento() != null &&
                        !os.getDataHoraEncerramento().toLocalDate().isBefore(dataInicio) &&
                        !os.getDataHoraEncerramento().toLocalDate().isAfter(dataFim))
                .collect(Collectors.toList());

        BigDecimal valorTotalServicos = BigDecimal.ZERO;
        BigDecimal descontoServicos = BigDecimal.ZERO;
        int totalServicosRealizados = 0;

        List<RelatorioComissaoDTO.OrdemServicoComissaoDTO> ordensComissao = new ArrayList<>();

        for (OrdemServico os : ordensEncerradas) {

            List<ServicoOrdemServico> servicosOS = servicoOrdemServicoRepository.findByNumeroOSOrderByDataRealizacaoDesc(os.getNumeroOS());

            boolean osPertenceAoMecanico = os.getMecanico() != null && os.getMecanico().getId().equals(mecanicoId);
            
            if (!osPertenceAoMecanico) {
                continue;
            }
            
            if (servicosOS.isEmpty()) {
                continue;
            }
            
            BigDecimal valorServicosOS = servicosOS.stream()
                    .map(s -> s.getValor() != null ? BigDecimal.valueOf(s.getValor()) : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal descontoOS = os.getDescontoServicos() != null 
                    ? BigDecimal.valueOf(os.getDescontoServicos()) 
                    : BigDecimal.ZERO;

            BigDecimal valorFinalOS = valorServicosOS.subtract(descontoOS);

            List<RelatorioComissaoDTO.ServicoRealizadoDTO> servicosDTO = servicosOS.stream()
                    .map(s -> new RelatorioComissaoDTO.ServicoRealizadoDTO(
                            s.getServico().getId(),
                            s.getServico().getNome(),
                            s.getValor() != null ? BigDecimal.valueOf(s.getValor()) : BigDecimal.ZERO,
                            BigDecimal.ZERO,
                            s.getDataRealizacao()
                    ))
                    .collect(Collectors.toList());

            ordensComissao.add(new RelatorioComissaoDTO.OrdemServicoComissaoDTO(
                    os.getId(),
                    os.getNumeroOS(),
                    os.getDataHora(),
                    os.getDataHoraEncerramento(),
                    os.getClienteNome(),
                    os.getVeiculoNome(),
                    os.getVeiculoPlaca(),
                    valorServicosOS,
                    descontoOS,
                    valorFinalOS,
                    servicosDTO
            ));

            valorTotalServicos = valorTotalServicos.add(valorServicosOS);
            descontoServicos = descontoServicos.add(descontoOS);
            totalServicosRealizados += servicosOS.size();
        }

        BigDecimal valorComissao = valorTotalServicos.subtract(descontoServicos);

        return new RelatorioComissaoDTO(
                dataInicio, dataFim, mecanicoId, mecanicoNome,
                valorTotalServicos, descontoServicos, valorComissao,
                ordensComissao.size(), totalServicosRealizados, ordensComissao
        );
    }

    public RelatorioGarantiasDTO gerarRelatorioGarantias(LocalDate dataInicio, LocalDate dataFim) {

        List<OrdemServico> ordensEncerradas = ordemServicoRepository.findAll().stream()
                .filter(os -> "Encerrada".equalsIgnoreCase(os.getStatus()) &&
                        os.getDataHoraEncerramento() != null)
                .collect(Collectors.toList());

        List<RelatorioGarantiasDTO.GarantiaItemDTO> garantias = new ArrayList<>();
        int garantiasEmAberto = 0;
        int garantiasEncerradas = 0;

        LocalDate dataAtual = LocalDate.now();

        for (OrdemServico os : ordensEncerradas) {
            LocalDate dataInicioGarantia = os.getDataHoraEncerramento().toLocalDate();
            LocalDate dataFimGarantia = dataInicioGarantia.plusMonths(os.getGarantiaMeses());

            boolean garantiaCoberta = !dataFimGarantia.isBefore(dataInicio) && !dataInicioGarantia.isAfter(dataFim);

            if (garantiaCoberta) {

                boolean emAberto = !dataFimGarantia.isBefore(dataAtual);
                String statusDescricao = emAberto ? "Em Aberto" : "Encerrada";

                if (emAberto) {
                    garantiasEmAberto++;
                } else {
                    garantiasEncerradas++;
                }

                String mecanicoNome = os.getMecanico() != null ? os.getMecanico().getNome() : null;
                String consultorNome = os.getConsultor() != null ? os.getConsultor().getNome() : null;

                RelatorioGarantiasDTO.GarantiaItemDTO item = RelatorioGarantiasDTO.GarantiaItemDTO.builder()
                        .id(os.getId())
                        .numeroOS(os.getNumeroOS())
                        .dataEncerramento(os.getDataHoraEncerramento())
                        .dataInicioGarantia(dataInicioGarantia)
                        .dataFimGarantia(dataFimGarantia)
                        .garantiaMeses(os.getGarantiaMeses())
                        .clienteNome(os.getClienteNome())
                        .clienteCpf(os.getClienteCpf())
                        .clienteTelefone(os.getClienteTelefone())
                        .veiculoNome(os.getVeiculoNome())
                        .veiculoPlaca(os.getVeiculoPlaca())
                        .veiculoMarca(os.getVeiculoMarca())
                        .valorTotal(os.getPrecoTotal() != null ? BigDecimal.valueOf(os.getPrecoTotal()) : BigDecimal.ZERO)
                        .mecanicoNome(mecanicoNome)
                        .consultorNome(consultorNome)
                        .emAberto(emAberto)
                        .statusDescricao(statusDescricao)
                        .build();

                garantias.add(item);
            }
        }

        garantias.sort((a, b) -> b.getDataEncerramento().compareTo(a.getDataEncerramento()));

        return RelatorioGarantiasDTO.builder()
                .dataInicio(dataInicio)
                .dataFim(dataFim)
                .totalGarantias(garantias.size())
                .garantiasEmAberto(garantiasEmAberto)
                .garantiasEncerradas(garantiasEncerradas)
                .garantias(garantias)
                .build();
    }

    public RelatorioFiadoDTO gerarRelatorioFiado(LocalDate dataInicio, LocalDate dataFim) {
        List<OrdemServico> ordensComFiado = ordemServicoRepository.findAll().stream()
                .filter(os -> "Encerrada".equalsIgnoreCase(os.getStatus()) &&
                        os.getDataHoraEncerramento() != null &&
                        os.getPrazoFiadoDias() != null &&
                        os.getPrazoFiadoDias() > 0)
                .collect(Collectors.toList());

        List<RelatorioFiadoDTO.FiadoItemDTO> fiados = new ArrayList<>();
        int fiadosNoPrazo = 0;
        int fiadosVencidos = 0;
        BigDecimal valorNoPrazo = BigDecimal.ZERO;
        BigDecimal valorVencido = BigDecimal.ZERO;

        LocalDate dataAtual = LocalDate.now();

        for (OrdemServico os : ordensComFiado) {
            LocalDate dataInicioFiado = os.getDataHoraEncerramento().toLocalDate();
            LocalDate dataVencimentoFiado = dataInicioFiado.plusDays(os.getPrazoFiadoDias());
            boolean fiadoNoPeriodo = !dataVencimentoFiado.isBefore(dataInicio) && !dataInicioFiado.isAfter(dataFim);

            if (fiadoNoPeriodo) {

                boolean noPrazo = !dataVencimentoFiado.isBefore(dataAtual);
                String statusDescricao = noPrazo ? "No Prazo" : "Vencido";

                BigDecimal valorTotal = os.getPrecoTotal() != null ? BigDecimal.valueOf(os.getPrecoTotal()) : BigDecimal.ZERO;

                if (noPrazo) {
                    fiadosNoPrazo++;
                    valorNoPrazo = valorNoPrazo.add(valorTotal);
                } else {
                    fiadosVencidos++;
                    valorVencido = valorVencido.add(valorTotal);
                }

                String mecanicoNome = os.getMecanico() != null ? os.getMecanico().getNome() : null;
                String consultorNome = os.getConsultor() != null ? os.getConsultor().getNome() : null;
                String tipoPagamentoNome = os.getTipoPagamento() != null ? os.getTipoPagamento().getNome() : null;

                RelatorioFiadoDTO.FiadoItemDTO item = RelatorioFiadoDTO.FiadoItemDTO.builder()
                        .id(os.getId())
                        .numeroOS(os.getNumeroOS())
                        .dataEncerramento(os.getDataHoraEncerramento())
                        .dataInicioFiado(dataInicioFiado)
                        .dataVencimentoFiado(dataVencimentoFiado)
                        .prazoFiadoDias(os.getPrazoFiadoDias())
                        .clienteNome(os.getClienteNome())
                        .clienteCpf(os.getClienteCpf())
                        .clienteTelefone(os.getClienteTelefone())
                        .veiculoNome(os.getVeiculoNome())
                        .veiculoPlaca(os.getVeiculoPlaca())
                        .veiculoMarca(os.getVeiculoMarca())
                        .valorTotal(valorTotal)
                        .mecanicoNome(mecanicoNome)
                        .consultorNome(consultorNome)
                        .tipoPagamentoNome(tipoPagamentoNome)
                        .noPrazo(noPrazo)
                        .statusDescricao(statusDescricao)
                        .build();

                fiados.add(item);
            }
        }

        fiados.sort((a, b) -> a.getDataVencimentoFiado().compareTo(b.getDataVencimentoFiado()));

        BigDecimal valorTotal = valorNoPrazo.add(valorVencido);

        return RelatorioFiadoDTO.builder()
                .dataInicio(dataInicio)
                .dataFim(dataFim)
                .totalFiados(fiados.size())
                .fiadosNoPrazo(fiadosNoPrazo)
                .fiadosVencidos(fiadosVencidos)
                .valorTotalFiado(valorTotal)
                .valorNoPrazo(valorNoPrazo)
                .valorVencido(valorVencido)
                .fiados(fiados)
                .build();
    }
}
