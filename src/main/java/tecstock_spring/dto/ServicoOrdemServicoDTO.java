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
public class ServicoOrdemServicoDTO {
    private Long id;
    private String numeroOS;
    private String servicoNome;
    private Long servicoId;
    private Double valor;
    private String categoriaVeiculo;
    private LocalDateTime dataRealizacao;
    private String observacoes;
}
