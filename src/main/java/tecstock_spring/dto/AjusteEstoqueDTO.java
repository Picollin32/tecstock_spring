package tecstock_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AjusteEstoqueDTO {
    private Long pecaId;
    private Integer ajuste;
    private String observacoes;
    private Double novoPrecoUnitario;
}
