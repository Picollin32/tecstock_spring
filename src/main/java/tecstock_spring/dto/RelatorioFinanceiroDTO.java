package tecstock_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
