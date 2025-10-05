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
public class RelatorioServicosDTO {
    
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private BigDecimal valorServicosRealizados;
    private Integer totalServicosRealizados;
    private List<ItemServicoDTO> servicosMaisRealizados;
    private Integer totalOrdensServico;
    private Integer ordensFinalizadas;
    private Integer ordensEmAndamento;
    private Integer ordensCanceladas;
    private BigDecimal descontoServicos;
    private BigDecimal valorMedioPorOrdem;
    private Double tempoMedioExecucao;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemServicoDTO {
        private Long idServico;
        private String nomeServico;
        private Integer quantidade;
        private BigDecimal valorTotal;
    }
}
