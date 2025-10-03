package tecstock_spring.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class RelatorioEstoqueDTO {
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Integer totalMovimentacoes;
    private Integer totalEntradas;
    private Integer totalSaidas;
    private BigDecimal valorTotalEstoque;
    private BigDecimal valorEntradas;
    private BigDecimal valorSaidas;
    private List<ItemEstoqueDTO> pecasMaisMovimentadas;
    private List<ItemEstoqueDTO> pecasEstoqueBaixo;

    public static class ItemEstoqueDTO {
        private Long idPeca;
        private String nomePeca;
        private Integer quantidade;
        private BigDecimal valor;

        public ItemEstoqueDTO() {
        }

        public ItemEstoqueDTO(Long idPeca, String nomePeca, Integer quantidade, BigDecimal valor) {
            this.idPeca = idPeca;
            this.nomePeca = nomePeca;
            this.quantidade = quantidade;
            this.valor = valor;
        }

        // Getters and Setters
        public Long getIdPeca() {
            return idPeca;
        }

        public void setIdPeca(Long idPeca) {
            this.idPeca = idPeca;
        }

        public String getNomePeca() {
            return nomePeca;
        }

        public void setNomePeca(String nomePeca) {
            this.nomePeca = nomePeca;
        }

        public Integer getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(Integer quantidade) {
            this.quantidade = quantidade;
        }

        public BigDecimal getValor() {
            return valor;
        }

        public void setValor(BigDecimal valor) {
            this.valor = valor;
        }
    }

    public RelatorioEstoqueDTO() {
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

    public Integer getTotalMovimentacoes() {
        return totalMovimentacoes;
    }

    public void setTotalMovimentacoes(Integer totalMovimentacoes) {
        this.totalMovimentacoes = totalMovimentacoes;
    }

    public Integer getTotalEntradas() {
        return totalEntradas;
    }

    public void setTotalEntradas(Integer totalEntradas) {
        this.totalEntradas = totalEntradas;
    }

    public Integer getTotalSaidas() {
        return totalSaidas;
    }

    public void setTotalSaidas(Integer totalSaidas) {
        this.totalSaidas = totalSaidas;
    }

    public BigDecimal getValorTotalEstoque() {
        return valorTotalEstoque;
    }

    public void setValorTotalEstoque(BigDecimal valorTotalEstoque) {
        this.valorTotalEstoque = valorTotalEstoque;
    }

    public BigDecimal getValorEntradas() {
        return valorEntradas;
    }

    public void setValorEntradas(BigDecimal valorEntradas) {
        this.valorEntradas = valorEntradas;
    }

    public BigDecimal getValorSaidas() {
        return valorSaidas;
    }

    public void setValorSaidas(BigDecimal valorSaidas) {
        this.valorSaidas = valorSaidas;
    }

    public List<ItemEstoqueDTO> getPecasMaisMovimentadas() {
        return pecasMaisMovimentadas;
    }

    public void setPecasMaisMovimentadas(List<ItemEstoqueDTO> pecasMaisMovimentadas) {
        this.pecasMaisMovimentadas = pecasMaisMovimentadas;
    }

    public List<ItemEstoqueDTO> getPecasEstoqueBaixo() {
        return pecasEstoqueBaixo;
    }

    public void setPecasEstoqueBaixo(List<ItemEstoqueDTO> pecasEstoqueBaixo) {
        this.pecasEstoqueBaixo = pecasEstoqueBaixo;
    }
}
