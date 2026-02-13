package tecstock_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrcamentoPesquisaDTO {
    private Long id;
    private String numeroOrcamento;
    private LocalDateTime dataHora;
    private String clienteNome;
    private String clienteCpf;
    private String veiculoNome;
    private String veiculoPlaca;
    private Double precoTotal;
    private String tipoPagamento;
    private Boolean transformadoEmOS;
    private String numeroOSGerado;
}
