package tecstock_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultorMetricasDTO {
    private Long consultorId;
    private String consultorNome;
    private Integer totalOrcamentos;
    private Integer totalOS;
    private Integer totalChecklists;
    private Integer totalAgendamentos;
    private Double valorTotalOS;
    private Double valorMedioOS;
    private Double taxaConversao;
}
