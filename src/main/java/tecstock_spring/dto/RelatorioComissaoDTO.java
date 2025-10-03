package tecstock_spring.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class RelatorioComissaoDTO {
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Long mecanicoId;
    private String mecanicoNome;
    private BigDecimal valorTotalServicos;
    private BigDecimal descontoServicos;
    private BigDecimal valorComissao;
    private Integer totalOrdensServico;
    private Integer totalServicosRealizados;
    private List<OrdemServicoComissaoDTO> ordensServico;

    public static class OrdemServicoComissaoDTO {
        private Long id;
        private String numeroOS;
        private LocalDateTime dataHora;
        private LocalDateTime dataHoraEncerramento;
        private String clienteNome;
        private String veiculoNome;
        private String veiculoPlaca;
        private BigDecimal valorServicos;
        private BigDecimal descontoServicos;
        private BigDecimal valorFinal;
        private List<ServicoRealizadoDTO> servicosRealizados;

        public OrdemServicoComissaoDTO() {
        }

        public OrdemServicoComissaoDTO(Long id, String numeroOS, LocalDateTime dataHora, 
                                       LocalDateTime dataHoraEncerramento, String clienteNome,
                                       String veiculoNome, String veiculoPlaca, 
                                       BigDecimal valorServicos, BigDecimal descontoServicos,
                                       BigDecimal valorFinal, List<ServicoRealizadoDTO> servicosRealizados) {
            this.id = id;
            this.numeroOS = numeroOS;
            this.dataHora = dataHora;
            this.dataHoraEncerramento = dataHoraEncerramento;
            this.clienteNome = clienteNome;
            this.veiculoNome = veiculoNome;
            this.veiculoPlaca = veiculoPlaca;
            this.valorServicos = valorServicos;
            this.descontoServicos = descontoServicos;
            this.valorFinal = valorFinal;
            this.servicosRealizados = servicosRealizados;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNumeroOS() {
            return numeroOS;
        }

        public void setNumeroOS(String numeroOS) {
            this.numeroOS = numeroOS;
        }

        public LocalDateTime getDataHora() {
            return dataHora;
        }

        public void setDataHora(LocalDateTime dataHora) {
            this.dataHora = dataHora;
        }

        public LocalDateTime getDataHoraEncerramento() {
            return dataHoraEncerramento;
        }

        public void setDataHoraEncerramento(LocalDateTime dataHoraEncerramento) {
            this.dataHoraEncerramento = dataHoraEncerramento;
        }

        public String getClienteNome() {
            return clienteNome;
        }

        public void setClienteNome(String clienteNome) {
            this.clienteNome = clienteNome;
        }

        public String getVeiculoNome() {
            return veiculoNome;
        }

        public void setVeiculoNome(String veiculoNome) {
            this.veiculoNome = veiculoNome;
        }

        public String getVeiculoPlaca() {
            return veiculoPlaca;
        }

        public void setVeiculoPlaca(String veiculoPlaca) {
            this.veiculoPlaca = veiculoPlaca;
        }

        public BigDecimal getValorServicos() {
            return valorServicos;
        }

        public void setValorServicos(BigDecimal valorServicos) {
            this.valorServicos = valorServicos;
        }

        public BigDecimal getDescontoServicos() {
            return descontoServicos;
        }

        public void setDescontoServicos(BigDecimal descontoServicos) {
            this.descontoServicos = descontoServicos;
        }

        public BigDecimal getValorFinal() {
            return valorFinal;
        }

        public void setValorFinal(BigDecimal valorFinal) {
            this.valorFinal = valorFinal;
        }

        public List<ServicoRealizadoDTO> getServicosRealizados() {
            return servicosRealizados;
        }

        public void setServicosRealizados(List<ServicoRealizadoDTO> servicosRealizados) {
            this.servicosRealizados = servicosRealizados;
        }
    }

    public static class ServicoRealizadoDTO {
        private Long idServico;
        private String nomeServico;
        private BigDecimal valor;
        private BigDecimal valorDesconto;
        private LocalDateTime dataRealizacao;

        public ServicoRealizadoDTO() {
        }

        public ServicoRealizadoDTO(Long idServico, String nomeServico, BigDecimal valor, 
                                   BigDecimal valorDesconto, LocalDateTime dataRealizacao) {
            this.idServico = idServico;
            this.nomeServico = nomeServico;
            this.valor = valor;
            this.valorDesconto = valorDesconto;
            this.dataRealizacao = dataRealizacao;
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

        public BigDecimal getValor() {
            return valor;
        }

        public void setValor(BigDecimal valor) {
            this.valor = valor;
        }

        public BigDecimal getValorDesconto() {
            return valorDesconto;
        }

        public void setValorDesconto(BigDecimal valorDesconto) {
            this.valorDesconto = valorDesconto;
        }

        public LocalDateTime getDataRealizacao() {
            return dataRealizacao;
        }

        public void setDataRealizacao(LocalDateTime dataRealizacao) {
            this.dataRealizacao = dataRealizacao;
        }
    }

    public RelatorioComissaoDTO() {
    }

    public RelatorioComissaoDTO(LocalDate dataInicio, LocalDate dataFim, Long mecanicoId,
                                String mecanicoNome, BigDecimal valorTotalServicos,
                                BigDecimal descontoServicos, BigDecimal valorComissao,
                                Integer totalOrdensServico, Integer totalServicosRealizados,
                                List<OrdemServicoComissaoDTO> ordensServico) {
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.mecanicoId = mecanicoId;
        this.mecanicoNome = mecanicoNome;
        this.valorTotalServicos = valorTotalServicos;
        this.descontoServicos = descontoServicos;
        this.valorComissao = valorComissao;
        this.totalOrdensServico = totalOrdensServico;
        this.totalServicosRealizados = totalServicosRealizados;
        this.ordensServico = ordensServico;
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

    public Long getMecanicoId() {
        return mecanicoId;
    }

    public void setMecanicoId(Long mecanicoId) {
        this.mecanicoId = mecanicoId;
    }

    public String getMecanicoNome() {
        return mecanicoNome;
    }

    public void setMecanicoNome(String mecanicoNome) {
        this.mecanicoNome = mecanicoNome;
    }

    public BigDecimal getValorTotalServicos() {
        return valorTotalServicos;
    }

    public void setValorTotalServicos(BigDecimal valorTotalServicos) {
        this.valorTotalServicos = valorTotalServicos;
    }

    public BigDecimal getDescontoServicos() {
        return descontoServicos;
    }

    public void setDescontoServicos(BigDecimal descontoServicos) {
        this.descontoServicos = descontoServicos;
    }

    public BigDecimal getValorComissao() {
        return valorComissao;
    }

    public void setValorComissao(BigDecimal valorComissao) {
        this.valorComissao = valorComissao;
    }

    public Integer getTotalOrdensServico() {
        return totalOrdensServico;
    }

    public void setTotalOrdensServico(Integer totalOrdensServico) {
        this.totalOrdensServico = totalOrdensServico;
    }

    public Integer getTotalServicosRealizados() {
        return totalServicosRealizados;
    }

    public void setTotalServicosRealizados(Integer totalServicosRealizados) {
        this.totalServicosRealizados = totalServicosRealizados;
    }

    public List<OrdemServicoComissaoDTO> getOrdensServico() {
        return ordensServico;
    }

    public void setOrdensServico(List<OrdemServicoComissaoDTO> ordensServico) {
        this.ordensServico = ordensServico;
    }
}
