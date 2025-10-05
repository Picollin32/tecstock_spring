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
public class RelatorioAgendamentosDTO {
    
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Integer totalAgendamentos;
    private Integer agendamentosPorMecanico;
    private List<AgendamentoPorDiaDTO> agendamentosPorDia;
    private List<AgendamentoPorMecanicoDTO> agendamentosPorMecanicoLista;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AgendamentoPorDiaDTO {
        private String data;
        private Integer quantidade;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AgendamentoPorMecanicoDTO {
        private String nomeMecanico;
        private Integer quantidade;
    }
}
