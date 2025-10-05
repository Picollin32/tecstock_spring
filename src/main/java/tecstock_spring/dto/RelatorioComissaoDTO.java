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
public class RelatorioComissaoDTO {
    
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Long mecanicoId;
    private String mecanicoNome;
    private BigDecimal valorTotalServicos;
    private BigDecimal descontoServicos;
    private BigDecimal valorComissao;
    private Integer totalOrdensServico;
    private Integer totalServicosRealizados;
    private List<OrdemServicoComissaoDTO> ordensServico;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrdemServicoComissaoDTO {
        private Long id;
        private String numeroOS;
        private LocalDateTime dataHora;
        private LocalDateTime dataHoraEncerramento;
        private String clienteNome;
        private String veiculoNome;
        private String veiculoPlaca;
        private BigDecimal valorServicos;
        private BigDecimal descontoServicos;
        private BigDecimal valorFinal;
        private List<ServicoRealizadoDTO> servicosRealizados;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ServicoRealizadoDTO {
        private Long idServico;
        private String nomeServico;
        private BigDecimal valor;
        private BigDecimal valorDesconto;
        private LocalDateTime dataRealizacao;
    }
}
