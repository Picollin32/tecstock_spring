package tecstock_spring.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RelatorioVendasDTO {
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Integer totalOrcamentos;
    private Integer orcamentosAprovados;
    private Integer orcamentosRecusados;
    private Integer orcamentosPendentes;
    private BigDecimal valorTotalOrcamentos;
    private BigDecimal valorTotalAprovado;
    private BigDecimal ticketMedio;
    private Double taxaConversao;

    public RelatorioVendasDTO() {
    }

    public RelatorioVendasDTO(LocalDate dataInicio, LocalDate dataFim, Integer totalOrcamentos,
                              Integer orcamentosAprovados, Integer orcamentosRecusados,
                              Integer orcamentosPendentes, BigDecimal valorTotalOrcamentos,
                              BigDecimal valorTotalAprovado, BigDecimal ticketMedio, Double taxaConversao) {
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.totalOrcamentos = totalOrcamentos;
        this.orcamentosAprovados = orcamentosAprovados;
        this.orcamentosRecusados = orcamentosRecusados;
        this.orcamentosPendentes = orcamentosPendentes;
        this.valorTotalOrcamentos = valorTotalOrcamentos;
        this.valorTotalAprovado = valorTotalAprovado;
        this.ticketMedio = ticketMedio;
        this.taxaConversao = taxaConversao;
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

    public Integer getTotalOrcamentos() {
        return totalOrcamentos;
    }

    public void setTotalOrcamentos(Integer totalOrcamentos) {
        this.totalOrcamentos = totalOrcamentos;
    }

    public Integer getOrcamentosAprovados() {
        return orcamentosAprovados;
    }

    public void setOrcamentosAprovados(Integer orcamentosAprovados) {
        this.orcamentosAprovados = orcamentosAprovados;
    }

    public Integer getOrcamentosRecusados() {
        return orcamentosRecusados;
    }

    public void setOrcamentosRecusados(Integer orcamentosRecusados) {
        this.orcamentosRecusados = orcamentosRecusados;
    }

    public Integer getOrcamentosPendentes() {
        return orcamentosPendentes;
    }

    public void setOrcamentosPendentes(Integer orcamentosPendentes) {
        this.orcamentosPendentes = orcamentosPendentes;
    }

    public BigDecimal getValorTotalOrcamentos() {
        return valorTotalOrcamentos;
    }

    public void setValorTotalOrcamentos(BigDecimal valorTotalOrcamentos) {
        this.valorTotalOrcamentos = valorTotalOrcamentos;
    }

    public BigDecimal getValorTotalAprovado() {
        return valorTotalAprovado;
    }

    public void setValorTotalAprovado(BigDecimal valorTotalAprovado) {
        this.valorTotalAprovado = valorTotalAprovado;
    }

    public BigDecimal getTicketMedio() {
        return ticketMedio;
    }

    public void setTicketMedio(BigDecimal ticketMedio) {
        this.ticketMedio = ticketMedio;
    }

    public Double getTaxaConversao() {
        return taxaConversao;
    }

    public void setTaxaConversao(Double taxaConversao) {
        this.taxaConversao = taxaConversao;
    }
}
