package tecstock_spring.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GarantiaResumoDTO {

    private Long id;
    private String numeroOS;
    private String clienteNome;
    private String veiculoNome;
    private String veiculoPlaca;
    private LocalDateTime dataEncerramento;
    private LocalDate dataInicioGarantia;
    private LocalDate dataFimGarantia;
    private Integer garantiaMeses;
    private String statusOS;
    private String statusGarantia;
    private String mecanicoNome;
    private String consultorNome;
    private List<GarantiaServicoDTO> servicos;
    private Long retornoId;
    private Long retornoServicoId;
    private String retornoServicoNome;
    private String retornoMotivo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GarantiaServicoDTO {
        private Long id;
        private String nome;
    }
}
