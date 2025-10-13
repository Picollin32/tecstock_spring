package tecstock_spring.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AjusteEstoqueDTO {
    
    @NotNull(message = "ID da peça é obrigatório")
    private Long pecaId;
    
    @NotNull(message = "Valor do ajuste é obrigatório")
    private Integer ajuste;
    
    private String observacoes;
    
    @Positive(message = "Preço unitário deve ser positivo")
    private Double novoPrecoUnitario;
}
