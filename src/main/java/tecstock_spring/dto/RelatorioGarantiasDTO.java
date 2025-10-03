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
    
    // Estatísticas gerais
    private Integer totalGarantias;
    private Integer garantiasEmAberto;
    private Integer garantiasEncerradas;
    
    // Lista de ordens com garantia
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
        
        // Status da garantia em relação à data pesquisada
        private Boolean emAberto; // true = verde (garantia coberta), false = vermelho (garantia expirada)
        private String statusDescricao; // "Em Aberto" ou "Encerrada"
    }
}
