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
public class RelatorioFiadoDTO {
    
    private LocalDate dataInicio;
    private LocalDate dataFim;
    
    // Estatísticas gerais
    private Integer totalFiados;
    private Integer fiadosNoPrazo;
    private Integer fiadosVencidos;
    private BigDecimal valorTotalFiado;
    private BigDecimal valorNoPrazo;
    private BigDecimal valorVencido;
    
    // Lista de ordens com fiado
    private List<FiadoItemDTO> fiados;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FiadoItemDTO {
        private Long id;
        private String numeroOS;
        private LocalDateTime dataEncerramento;
        private LocalDate dataInicioFiado;
        private LocalDate dataVencimentoFiado;
        private Integer prazoFiadoDias;
        private String clienteNome;
        private String clienteCpf;
        private String clienteTelefone;
        private String veiculoNome;
        private String veiculoPlaca;
        private String veiculoMarca;
        private BigDecimal valorTotal;
        private String mecanicoNome;
        private String consultorNome;
        private String tipoPagamentoNome;
        
        // Status do fiado em relação à data atual
        private Boolean noPrazo; // true = verde (no prazo), false = vermelho (vencido)
        private String statusDescricao; // "No Prazo" ou "Vencido"
    }
}
