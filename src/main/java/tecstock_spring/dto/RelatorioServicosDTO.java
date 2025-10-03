package tecstock_spring.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class RelatorioServicosDTO {
    private LocalDate dataInicio;
    private LocalDate dataFim;
    
    // Seção: Serviços Realizados
    private BigDecimal valorServicosRealizados; // valor - desconto em serviços
    private Integer totalServicosRealizados;
    private List<ItemServicoDTO> servicosMaisRealizados;
    
    // Seção: Ordem de Serviço
    private Integer totalOrdensServico;
    private Integer ordensFinalizadas;
    private Integer ordensEmAndamento;
    private Integer ordensCanceladas;
    
    // Campos adicionais
    private BigDecimal descontoServicos;
    private BigDecimal valorMedioPorOrdem;
    private Double tempoMedioExecucao; // em dias

    public static class ItemServicoDTO {
        private Long idServico;
        private String nomeServico;
        private Integer quantidade;
        private BigDecimal valorTotal;

        public ItemServicoDTO() {
        }

        public ItemServicoDTO(Long idServico, String nomeServico, Integer quantidade, BigDecimal valorTotal) {
            this.idServico = idServico;
            this.nomeServico = nomeServico;
            this.quantidade = quantidade;
            this.valorTotal = valorTotal;
        }

        // Getters and Setters
        public Long getIdServico() {
            return idServico;
        }

        public void setIdServico(Long idServico) {
            this.idServico = idServico;
        }

        public String getNomeServico() {
            return nomeServico;
        }

        public void setNomeServico(String nomeServico) {
            this.nomeServico = nomeServico;
        }

        public Integer getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(Integer quantidade) {
            this.quantidade = quantidade;
        }

        public BigDecimal getValorTotal() {
            return valorTotal;
        }

        public void setValorTotal(BigDecimal valorTotal) {
            this.valorTotal = valorTotal;
        }
    }

    public RelatorioServicosDTO() {
    }

    public RelatorioServicosDTO(LocalDate dataInicio, LocalDate dataFim, 
                                BigDecimal valorServicosRealizados, Integer totalServicosRealizados,
                                List<ItemServicoDTO> servicosMaisRealizados,
                                Integer totalOrdensServico, Integer ordensFinalizadas, 
                                Integer ordensEmAndamento, Integer ordensCanceladas,
                                BigDecimal descontoServicos, BigDecimal valorMedioPorOrdem, 
                                Double tempoMedioExecucao) {
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.valorServicosRealizados = valorServicosRealizados;
        this.totalServicosRealizados = totalServicosRealizados;
        this.servicosMaisRealizados = servicosMaisRealizados;
        this.totalOrdensServico = totalOrdensServico;
        this.ordensFinalizadas = ordensFinalizadas;
        this.ordensEmAndamento = ordensEmAndamento;
        this.ordensCanceladas = ordensCanceladas;
        this.descontoServicos = descontoServicos;
        this.valorMedioPorOrdem = valorMedioPorOrdem;
        this.tempoMedioExecucao = tempoMedioExecucao;
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

    public Integer getTotalOrdensServico() {
        return totalOrdensServico;
    }

    public void setTotalOrdensServico(Integer totalOrdensServico) {
        this.totalOrdensServico = totalOrdensServico;
    }

    public Integer getOrdensFinalizadas() {
        return ordensFinalizadas;
    }

    public void setOrdensFinalizadas(Integer ordensFinalizadas) {
        this.ordensFinalizadas = ordensFinalizadas;
    }

    public Integer getOrdensEmAndamento() {
        return ordensEmAndamento;
    }

    public void setOrdensEmAndamento(Integer ordensEmAndamento) {
        this.ordensEmAndamento = ordensEmAndamento;
    }

    public Integer getOrdensCanceladas() {
        return ordensCanceladas;
    }

    public void setOrdensCanceladas(Integer ordensCanceladas) {
        this.ordensCanceladas = ordensCanceladas;
    }

    public BigDecimal getValorServicosRealizados() {
        return valorServicosRealizados;
    }

    public void setValorServicosRealizados(BigDecimal valorServicosRealizados) {
        this.valorServicosRealizados = valorServicosRealizados;
    }

    public BigDecimal getDescontoServicos() {
        return descontoServicos;
    }

    public void setDescontoServicos(BigDecimal descontoServicos) {
        this.descontoServicos = descontoServicos;
    }

    public BigDecimal getValorMedioPorOrdem() {
        return valorMedioPorOrdem;
    }

    public void setValorMedioPorOrdem(BigDecimal valorMedioPorOrdem) {
        this.valorMedioPorOrdem = valorMedioPorOrdem;
    }

    public Double getTempoMedioExecucao() {
        return tempoMedioExecucao;
    }

    public void setTempoMedioExecucao(Double tempoMedioExecucao) {
        this.tempoMedioExecucao = tempoMedioExecucao;
    }

    public Integer getTotalServicosRealizados() {
        return totalServicosRealizados;
    }

    public void setTotalServicosRealizados(Integer totalServicosRealizados) {
        this.totalServicosRealizados = totalServicosRealizados;
    }

    public List<ItemServicoDTO> getServicosMaisRealizados() {
        return servicosMaisRealizados;
    }

    public void setServicosMaisRealizados(List<ItemServicoDTO> servicosMaisRealizados) {
        this.servicosMaisRealizados = servicosMaisRealizados;
    }
}
