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
public class TipoPagamentoPesquisaDTO {
    private Long id;
    private String nome;
    private Integer codigo;
    private Integer idFormaPagamento;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
