package tecstock_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelatorioConsultoresDTO {
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private List<ConsultorMetricasDTO> consultores;
    private Integer totalOrcamentosGeral;
    private Integer totalOSGeral;
    private Integer totalChecklistsGeral;
    private Double valorTotalGeral;
    private Double valorMedioGeral;
    private Double taxaConversaoGeral;
}
