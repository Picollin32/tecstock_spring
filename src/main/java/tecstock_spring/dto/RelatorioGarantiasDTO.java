package tecstock_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelatorioGarantiasDTO {
    
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Integer totalGarantias;
    private Integer garantiasEmAberto;
    private Integer garantiasEncerradas;
    private List<GarantiaItemDTO> garantias;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GarantiaItemDTO {
        private Long id;
        private String numeroOS;
        private LocalDateTime dataEncerramento;
        private LocalDate dataInicioGarantia;
        private LocalDate dataFimGarantia;
        private Integer garantiaMeses;
        private String clienteNome;
        private String clienteCpf;
        private String clienteTelefone;
        private String veiculoNome;
        private String veiculoPlaca;
        private String veiculoMarca;
        private BigDecimal valorTotal;
        private String mecanicoNome;
        private String consultorNome;
        private Boolean emAberto;
        private String statusDescricao;
    }
}
