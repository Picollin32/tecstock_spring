package tecstock_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdemServicoResumoDTO {
    
    private Long id;
    private String numeroOS;
    private LocalDateTime dataHora;
    private String clienteNome;
    private String clienteCpf;
    private String veiculoNome;
    private String veiculoPlaca;
    private Double precoTotal;
    private Double precoTotalServicos;
    private Double precoTotalPecas;
    private Double descontoServicos;
    private Double descontoPecas;
    private String status;
    private Integer quantidadeServicos;
    private String tipoPagamento;
    private Integer garantiaMeses;
    private String nomeMecanico;
    private String nomeConsultor;
    private Integer numeroParcelas;
    private LocalDateTime createdAt;
    private String observacoes;
}
