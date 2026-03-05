package tecstock_spring.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tecstock_spring.model.Conta;
import tecstock_spring.model.Empresa;
import tecstock_spring.model.OrdemServico;
import tecstock_spring.repository.ContaRepository;
import tecstock_spring.repository.EmpresaRepository;
import tecstock_spring.util.TenantContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.springframework.lang.NonNull;

@Service
@RequiredArgsConstructor
public class ContaServiceImpl implements ContaService {

    private final ContaRepository contaRepository;
    private final EmpresaRepository empresaRepository;
    private static final Logger logger = LoggerFactory.getLogger(ContaServiceImpl.class);

    @Override
    @Transactional
    public void gerarContasParaOS(OrdemServico os) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            logger.warn("EmpresaId nulo ao tentar gerar contas para OS {}. Ignorando.", os.getNumeroOS());
            return;
        }

        List<Conta> existentes = contaRepository.findByEmpresaIdAndOrdemServicoId(empresaId, os.getId());
        if (!existentes.isEmpty()) {
            logger.info("Contas já geradas para OS {}. Ignorando geração duplicada.", os.getNumeroOS());
            return;
        }

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        LocalDateTime encerramento = os.getDataHoraEncerramento() != null
                ? os.getDataHoraEncerramento()
                : LocalDateTime.now();

        int mesBase = encerramento.getMonthValue();
        int anoBase = encerramento.getYear();
        Double valorTotal = os.getPrecoTotal() != null ? os.getPrecoTotal() : 0.0;

        boolean isFiado = os.getPrazoFiadoDias() != null && os.getPrazoFiadoDias() > 0;
        boolean isCredito = !isFiado && os.getNumeroParcelas() != null && os.getNumeroParcelas() > 1;

        if (isFiado) {
            gerarContasFiado(os, empresa, mesBase, anoBase, valorTotal, encerramento);
        } else if (isCredito) {
            gerarContasCredito(os, empresa, mesBase, anoBase, valorTotal, encerramento);
        } else {
            gerarContaAvista(os, empresa, mesBase, anoBase, valorTotal, encerramento);
        }
    }

    private void gerarContaAvista(OrdemServico os, Empresa empresa,
                                   int mes, int ano, double valor, LocalDateTime encerramento) {
        String nomePagamento = os.getTipoPagamento() != null ? os.getTipoPagamento().getNome() : "Pagamento";
        String descricao = "OS #" + os.getNumeroOS() + " – " + os.getClienteNome()
                + " (" + nomePagamento + ")";

        Conta conta = Conta.builder()
                .empresa(empresa)
                .tipo("A_RECEBER")
                .descricao(descricao)
                .valor(valor)
                .mesReferencia(mes)
                .anoReferencia(ano)
                .dataVencimento(encerramento.toLocalDate())
                .pago(true)
                .dataPagamento(encerramento)
                .ordemServicoId(os.getId())
                .ordemServicoNumero(os.getNumeroOS())
                .origemTipo("OS_AVISTA")
                .build();

        contaRepository.save(Objects.requireNonNull(conta));
        logger.info("Conta à vista gerada para OS {} – R$ {}", os.getNumeroOS(), valor);
    }

    private void gerarContasCredito(OrdemServico os, Empresa empresa,
                                     int mesBase, int anoBase, double valorTotal,
                                     LocalDateTime encerramento) {
        int parcelas = os.getNumeroParcelas();
        double valorParcela = Math.round((valorTotal / parcelas) * 100.0) / 100.0;
        String nomePagamento = os.getTipoPagamento() != null ? os.getTipoPagamento().getNome() : "Crédito";

        for (int i = 0; i < parcelas; i++) {
            int mes = mesBase + i;
            int ano = anoBase;
            while (mes > 12) {
                mes -= 12;
                ano++;
            }

            LocalDate vencimento = LocalDate.of(ano, mes, Math.min(encerramento.getDayOfMonth(),
                    LocalDate.of(ano, mes, 1).lengthOfMonth()));

            String descricao = "OS #" + os.getNumeroOS() + " – " + os.getClienteNome()
                    + " (" + nomePagamento + " " + (i + 1) + "/" + parcelas + ")";

            Conta conta = Conta.builder()
                    .empresa(empresa)
                    .tipo("A_RECEBER")
                    .descricao(descricao)
                    .valor(valorParcela)
                    .mesReferencia(mes)
                    .anoReferencia(ano)
                    .dataVencimento(vencimento)
                    .pago(true)
                    .dataPagamento(encerramento)
                    .ordemServicoId(os.getId())
                    .ordemServicoNumero(os.getNumeroOS())
                    .parcelaNumero(i + 1)
                    .totalParcelas(parcelas)
                    .origemTipo("OS_CREDITO")
                    .build();

            contaRepository.save(Objects.requireNonNull(conta));
        }
        logger.info("Contas de crédito geradas para OS {} – {} parcelas de R$ {}",
                os.getNumeroOS(), parcelas, valorParcela);
    }

    private void gerarContasFiado(OrdemServico os, Empresa empresa,
                                   int mesBase, int anoBase, double valorTotal,
                                   LocalDateTime encerramento) {
        int diasPrazo = os.getPrazoFiadoDias();

        int totalMeses = (int) Math.ceil(diasPrazo / 30.0);
        if (totalMeses < 1) totalMeses = 1;

        String grupoId = UUID.randomUUID().toString();

        for (int i = 0; i < totalMeses; i++) {
            int mes = mesBase + i;
            int ano = anoBase;
            while (mes > 12) {
                mes -= 12;
                ano++;
            }

            LocalDate vencimento = LocalDate.of(ano, mes, Math.min(encerramento.getDayOfMonth(),
                    LocalDate.of(ano, mes, 1).lengthOfMonth()));

            String descricao = "Fiado – OS #" + os.getNumeroOS() + " – " + os.getClienteNome()
                    + " (mês " + (i + 1) + "/" + totalMeses + ")";

            Conta conta = Conta.builder()
                    .empresa(empresa)
                    .tipo("A_RECEBER")
                    .descricao(descricao)
                    .valor(valorTotal)
                    .mesReferencia(mes)
                    .anoReferencia(ano)
                    .dataVencimento(vencimento)
                    .pago(false)
                    .ordemServicoId(os.getId())
                    .ordemServicoNumero(os.getNumeroOS())
                    .origemTipo("OS_FIADO")
                    .fiadoGrupoId(grupoId)
                    .build();

            contaRepository.save(Objects.requireNonNull(conta));
        }
        logger.info("Contas de fiado geradas para OS {} – {} meses (R$ {} cada) – grupoId: {}",
                os.getNumeroOS(), totalMeses, valorTotal, grupoId);
    }

    @Override
    public List<Conta> listarPorMesAno(int mes, int ano) {
        Long empresaId = requireEmpresaId();
        return contaRepository.findByEmpresaIdAndMesReferenciaAndAnoReferenciaOrderByDataVencimentoAsc(
                empresaId, mes, ano);
    }

    @Override
    public List<Conta> listarAPagarPorMesAno(int mes, int ano) {
        Long empresaId = requireEmpresaId();
        return contaRepository.findByEmpresaIdAndTipoAndMesReferenciaAndAnoReferenciaOrderByDataVencimentoAsc(
                empresaId, "A_PAGAR", mes, ano);
    }

    @Override
    public List<Conta> listarAReceberPorMesAno(int mes, int ano) {
        Long empresaId = requireEmpresaId();
        return contaRepository.findByEmpresaIdAndTipoAndMesReferenciaAndAnoReferenciaOrderByDataVencimentoAsc(
                empresaId, "A_RECEBER", mes, ano);
    }

    @Override
    public List<Conta> listarAtrasadas() {
        Long empresaId = requireEmpresaId();
        return contaRepository.findContasAtrasadas(empresaId, LocalDate.now());
    }

    @Override
    @Transactional
    public Conta adicionarContaPagar(Conta conta) {
        Long empresaId = requireEmpresaId();
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        java.time.YearMonth agora = java.time.YearMonth.now();
        java.time.YearMonth referencia = java.time.YearMonth.of(conta.getAnoReferencia(), conta.getMesReferencia());
        if (referencia.isBefore(agora)) {
            throw new IllegalArgumentException("Não é possível adicionar contas em mêses passados.");
        }

        conta.setEmpresa(empresa);
        conta.setTipo("A_PAGAR");
        conta.setOrigemTipo("MANUAL");
        if (conta.getPago() == null) conta.setPago(false);
        return contaRepository.save(conta);
    }

    @Override
    @Transactional
    public Conta marcarComoPago(Long id) {
        Long empresaId = requireEmpresaId();
        Conta conta = contaRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new RuntimeException("Conta não encontrada: " + id));

        validarEmpresa(conta, empresaId);

        conta.setPago(true);
        conta.setDataPagamento(LocalDateTime.now());
        Conta salva = contaRepository.save(conta);

        if ("OS_FIADO".equals(conta.getOrigemTipo()) && conta.getFiadoGrupoId() != null) {
            List<Conta> futuras = contaRepository.findFiadoEntradasFuturas(
                    empresaId, conta.getFiadoGrupoId(),
                    conta.getMesReferencia(), conta.getAnoReferencia());
            if (!futuras.isEmpty()) {
                contaRepository.deleteAll(Objects.requireNonNull(futuras));
                logger.info("Removidas {} entradas futuras do fiado grupo {}", futuras.size(), conta.getFiadoGrupoId());
            }
        }

        return salva;
    }

    @Override
    @Transactional
    public Conta desmarcarPagamento(Long id) {
        Long empresaId = requireEmpresaId();
        Conta conta = contaRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new RuntimeException("Conta não encontrada: " + id));
        validarEmpresa(conta, empresaId);

        if ("A_RECEBER".equals(conta.getTipo()) && !"OS_FIADO".equals(conta.getOrigemTipo())) {
            throw new IllegalArgumentException("Não é possível desmarcar recebimento de OS que não seja fiado.");
        }
        conta.setPago(false);
        conta.setDataPagamento(null);
        return contaRepository.save(conta);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        Long empresaId = requireEmpresaId();
        Conta conta = contaRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new RuntimeException("Conta não encontrada: " + id));
        validarEmpresa(conta, empresaId);

        if ("OS_FIADO".equals(conta.getOrigemTipo()) && conta.getFiadoGrupoId() != null) {
            List<Conta> grupo = contaRepository.findByEmpresaIdAndFiadoGrupoId(empresaId, conta.getFiadoGrupoId());
            contaRepository.deleteAll(Objects.requireNonNull(grupo));
            logger.info("Removido grupo de fiado {} ({} entradas)", conta.getFiadoGrupoId(), grupo.size());
            return;
        }

        contaRepository.deleteById(Objects.requireNonNull(id));
    }
    @Override
    @Transactional
    public Conta editar(Long id, Conta dados) {
        Long empresaId = requireEmpresaId();
        Conta conta = contaRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new RuntimeException("Conta não encontrada: " + id));
        validarEmpresa(conta, empresaId);
        if (!"A_PAGAR".equals(conta.getTipo())) {
            throw new IllegalArgumentException("Somente contas A_PAGAR podem ser editadas.");
        }
        if (Boolean.TRUE.equals(conta.getPago())) {
            throw new IllegalArgumentException("Não é possível editar uma conta já paga.");
        }
        if (dados.getDescricao() != null) conta.setDescricao(dados.getDescricao());
        if (dados.getValor() != null) conta.setValor(dados.getValor());
        if (dados.getDataVencimento() != null) {
            conta.setDataVencimento(dados.getDataVencimento());
            conta.setMesReferencia(dados.getDataVencimento().getMonthValue());
            conta.setAnoReferencia(dados.getDataVencimento().getYear());
        }
        return contaRepository.save(conta);
    }

    @Override
    @Transactional
    public void removerContasDaOS(Long osId) {
        Long empresaId = requireEmpresaId();
        List<Conta> contas = contaRepository.findByEmpresaIdAndOrdemServicoId(empresaId, osId);
        if (!contas.isEmpty()) {
            contaRepository.deleteAll(Objects.requireNonNull(contas));
            logger.info("Removidas {} contas da OS id={} após reabertura", contas.size(), osId);
        }
    }

    @Override
    public Map<String, Double> resumoMes(int mes, int ano) {
        List<Conta> contas = listarPorMesAno(mes, ano);

        double totalAPagar = contas.stream()
                .filter(c -> "A_PAGAR".equals(c.getTipo()))
                .mapToDouble(Conta::getValor)
                .sum();

        double totalAPagarPago = contas.stream()
                .filter(c -> "A_PAGAR".equals(c.getTipo()) && Boolean.TRUE.equals(c.getPago()))
                .mapToDouble(Conta::getValor)
                .sum();

        double totalAReceber = contas.stream()
                .filter(c -> "A_RECEBER".equals(c.getTipo()))
                .mapToDouble(Conta::getValor)
                .sum();

        double totalRecebido = contas.stream()
                .filter(c -> "A_RECEBER".equals(c.getTipo()) && Boolean.TRUE.equals(c.getPago()))
                .mapToDouble(Conta::getValor)
                .sum()
                + contas.stream()
                .filter(c -> "A_RECEBER".equals(c.getTipo()) && !Boolean.TRUE.equals(c.getPago())
                        && c.getValorPagoParcial() != null && c.getValorPagoParcial() > 0)
                .mapToDouble(Conta::getValorPagoParcial)
                .sum();

        Map<String, Double> resumo = new HashMap<>();
        resumo.put("totalAPagar", totalAPagar);
        resumo.put("totalAPagarPago", totalAPagarPago);
        resumo.put("totalAReceber", totalAReceber);
        resumo.put("totalRecebido", totalRecebido);
        resumo.put("saldo", totalRecebido - totalAPagarPago);
        return resumo;
    }

    @Override
    @Transactional
    public Conta registrarPagamentoParcial(Long id, Double valorPago) {
        Long empresaId = requireEmpresaId();
        Conta conta = contaRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new RuntimeException("Conta não encontrada: " + id));
        validarEmpresa(conta, empresaId);

        if (!"OS_FIADO".equals(conta.getOrigemTipo())) {
            throw new IllegalArgumentException("Pagamento parcial disponível apenas para fiados.");
        }
        if (Boolean.TRUE.equals(conta.getPago())) {
            throw new IllegalArgumentException("Este fiado já foi integralmente pago.");
        }
        if (valorPago == null || valorPago <= 0) {
            throw new IllegalArgumentException("Informe um valor de pagamento válido.");
        }

        double jaFoiPago = conta.getValorPagoParcial() != null ? conta.getValorPagoParcial() : 0.0;
        double saldoDevedor = conta.getValor() - jaFoiPago;

        if (valorPago > saldoDevedor + 0.001) {
            throw new IllegalArgumentException(
                    String.format("Valor informado (R$ %.2f) excede o saldo devedor (R$ %.2f).", valorPago, saldoDevedor));
        }

        double novoTotalPago = jaFoiPago + valorPago;
        double novoSaldo = conta.getValor() - novoTotalPago;

        if (novoSaldo <= 0.001) {

            conta.setValorPagoParcial(conta.getValor());
            conta.setPago(true);
            conta.setDataPagamento(LocalDateTime.now());
            Conta salva = contaRepository.save(conta);

            if (conta.getFiadoGrupoId() != null) {
                List<Conta> futuras = contaRepository.findFiadoEntradasFuturas(
                        empresaId, conta.getFiadoGrupoId(),
                        conta.getMesReferencia(), conta.getAnoReferencia());
                if (!futuras.isEmpty()) {
                    contaRepository.deleteAll(Objects.requireNonNull(futuras));
                    logger.info("Fiado grupo {} quitado via pagamento parcial. {} entradas futuras removidas.",
                            conta.getFiadoGrupoId(), futuras.size());
                }
            }
            return salva;
        } else {

            conta.setValorPagoParcial(novoTotalPago);
            Conta salva = contaRepository.save(conta);

            if (conta.getFiadoGrupoId() != null) {
                List<Conta> futuras = contaRepository.findFiadoEntradasFuturas(
                        empresaId, conta.getFiadoGrupoId(),
                        conta.getMesReferencia(), conta.getAnoReferencia());
                for (Conta futura : futuras) {
                    futura.setValor(novoSaldo);
                    futura.setValorPagoParcial(0.0);
                    contaRepository.save(futura);
                }
                logger.info("Pagamento parcial de R$ {} registrado no fiado grupo {}. Saldo restante: R$ {}. {} entradas futuras atualizadas.",
                        valorPago, conta.getFiadoGrupoId(), novoSaldo, futuras.size());
            }
            return salva;
        }
    }

    private @NonNull Long requireEmpresaId() {
        Long id = TenantContext.getCurrentEmpresaId();
        if (id == null) throw new IllegalStateException("EmpresaId não encontrado no contexto");
        return Objects.requireNonNull(id);
    }

    private void validarEmpresa(Conta conta, Long empresaId) {
        if (!conta.getEmpresa().getId().equals(empresaId)) {
            throw new SecurityException("Acesso negado: conta pertence a outra empresa");
        }
    }
}
