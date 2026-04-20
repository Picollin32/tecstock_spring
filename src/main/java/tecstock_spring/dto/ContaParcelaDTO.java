package tecstock_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContaParcelaDTO {

    private Long id;
    private Long contaId;
    private String tipoConta;
    private String descricaoConta;
    private String origemTipo;

    private Integer parcelaNumero;
    private Integer totalParcelas;

    private Double valor;
    private LocalDate dataVencimento;
    private Boolean pago;
    private LocalDateTime dataPagamento;
    private Double acrescimo;
    private Double desconto;

    private Long categoriaId;
    private String categoriaNome;

    private Long fornecedorId;
    private String fornecedorNome;
}
