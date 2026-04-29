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
public class RelatorioVeiculosDTO {

    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Integer totalVeiculos;
    private List<VeiculoItemDTO> veiculos;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VeiculoItemDTO {
        private String veiculoPlaca;
        private String veiculoNome;
        private String veiculoMarca;
        private String veiculoAno;
        private String proprietarioNome;
        private String proprietarioCpf;
        private Integer totalOS;
        private Integer totalOSEncerradas;
        private BigDecimal valorTotal;
        private LocalDate primeiraVisita;
        private LocalDate ultimaVisita;
    }
}
