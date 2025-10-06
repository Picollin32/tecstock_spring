package tecstock_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para ajuste de estoque de peças
 * Usado quando o admin faz reajustes manuais de quantidade
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AjusteEstoqueDTO {
    private Long pecaId;
    private Integer ajuste; // Valor positivo para adicionar, negativo para subtrair
    private String observacoes; // Motivo do ajuste
}
