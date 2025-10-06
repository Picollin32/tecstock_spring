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
    private Integer totalFiados;
    private Integer fiadosNoPrazo;
    private Integer fiadosVencidos;
    private Integer fiadosPagos;
    private Integer fiadosNaoPagos;
    private Integer fiadosNoPrazoPagos;
    private Integer fiadosNoPrazoNaoPagos;
    private Integer fiadosAtrasadosPagos;
    private Integer fiadosAtrasadosNaoPagos;
    private BigDecimal valorTotalFiado;
    private BigDecimal valorNoPrazo;
    private BigDecimal valorVencido;
    private BigDecimal valorPago;
    private BigDecimal valorNaoPago;
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
        private Boolean noPrazo;
        private Boolean fiadoPago;
        private String statusDescricao;
    }
}
