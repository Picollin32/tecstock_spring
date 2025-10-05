package tecstock_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemEstoqueDTO {
        private Long idPeca;
        private String nomePeca;
        private Integer quantidade;
        private BigDecimal valor;
    }
}
