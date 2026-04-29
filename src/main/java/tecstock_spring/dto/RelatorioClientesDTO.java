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
public class RelatorioClientesDTO {

    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Integer totalClientes;
    private List<ClienteItemDTO> clientes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClienteItemDTO {
        private String clienteNome;
        private String clienteCpf;
        private String clienteTelefone;
        private Integer totalOS;
        private Integer totalOSEncerradas;
        private BigDecimal valorTotal;
        private BigDecimal ticketMedio;
        private LocalDate primeiraVisita;
        private LocalDate ultimaVisita;
        private List<String> placasVeiculos;
    }
}
