package tecstock_spring.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class RelatorioFinanceiroDTO {
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private BigDecimal receitaTotal;
    private BigDecimal receitaServicos;
    private BigDecimal receitaPecas;
    private BigDecimal despesasEstoque;
    private BigDecimal descontosPecas;
    private BigDecimal descontosServicos;
    private BigDecimal descontosTotal;
    private BigDecimal lucroEstimado;
    private Map<String, BigDecimal> receitaPorTipoPagamento;
    private Map<String, Integer> quantidadePorTipoPagamento;
    private BigDecimal ticketMedio;

    public RelatorioFinanceiroDTO() {
    }

    public RelatorioFinanceiroDTO(LocalDate dataInicio, LocalDate dataFim, BigDecimal receitaTotal,
                                  BigDecimal receitaServicos, BigDecimal receitaPecas,
                                  BigDecimal despesasEstoque, BigDecimal descontosPecas,
                                  BigDecimal descontosServicos, BigDecimal descontosTotal,
                                  BigDecimal lucroEstimado,
                                  Map<String, BigDecimal> receitaPorTipoPagamento,
                                  Map<String, Integer> quantidadePorTipoPagamento,
                                  BigDecimal ticketMedio) {
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.receitaTotal = receitaTotal;
        this.receitaServicos = receitaServicos;
        this.receitaPecas = receitaPecas;
        this.despesasEstoque = despesasEstoque;
        this.descontosPecas = descontosPecas;
        this.descontosServicos = descontosServicos;
        this.descontosTotal = descontosTotal;
        this.lucroEstimado = lucroEstimado;
        this.receitaPorTipoPagamento = receitaPorTipoPagamento;
        this.quantidadePorTipoPagamento = quantidadePorTipoPagamento;
        this.ticketMedio = ticketMedio;
    }

    // Getters and Setters
    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public BigDecimal getReceitaTotal() {
        return receitaTotal;
    }

    public void setReceitaTotal(BigDecimal receitaTotal) {
        this.receitaTotal = receitaTotal;
    }

    public BigDecimal getReceitaServicos() {
        return receitaServicos;
    }

    public void setReceitaServicos(BigDecimal receitaServicos) {
        this.receitaServicos = receitaServicos;
    }

    public BigDecimal getReceitaPecas() {
        return receitaPecas;
    }

    public void setReceitaPecas(BigDecimal receitaPecas) {
        this.receitaPecas = receitaPecas;
    }

    public BigDecimal getDespesasEstoque() {
        return despesasEstoque;
    }

    public void setDespesasEstoque(BigDecimal despesasEstoque) {
        this.despesasEstoque = despesasEstoque;
    }

    public BigDecimal getDescontosPecas() {
        return descontosPecas;
    }

    public void setDescontosPecas(BigDecimal descontosPecas) {
        this.descontosPecas = descontosPecas;
    }

    public BigDecimal getDescontosServicos() {
        return descontosServicos;
    }

    public void setDescontosServicos(BigDecimal descontosServicos) {
        this.descontosServicos = descontosServicos;
    }

    public BigDecimal getDescontosTotal() {
        return descontosTotal;
    }

    public void setDescontosTotal(BigDecimal descontosTotal) {
        this.descontosTotal = descontosTotal;
    }

    public BigDecimal getLucroEstimado() {
        return lucroEstimado;
    }

    public void setLucroEstimado(BigDecimal lucroEstimado) {
        this.lucroEstimado = lucroEstimado;
    }

    public Map<String, BigDecimal> getReceitaPorTipoPagamento() {
        return receitaPorTipoPagamento;
    }

    public void setReceitaPorTipoPagamento(Map<String, BigDecimal> receitaPorTipoPagamento) {
        this.receitaPorTipoPagamento = receitaPorTipoPagamento;
    }

    public Map<String, Integer> getQuantidadePorTipoPagamento() {
        return quantidadePorTipoPagamento;
    }

    public void setQuantidadePorTipoPagamento(Map<String, Integer> quantidadePorTipoPagamento) {
        this.quantidadePorTipoPagamento = quantidadePorTipoPagamento;
    }

    public BigDecimal getTicketMedio() {
        return ticketMedio;
    }

    public void setTicketMedio(BigDecimal ticketMedio) {
        this.ticketMedio = ticketMedio;
    }
}
