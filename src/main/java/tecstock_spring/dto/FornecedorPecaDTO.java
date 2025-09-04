package tecstock_spring.dto;

import lombok.Data;

@Data
public class FornecedorPecaDTO {
    private Long fornecedorId;
    private Long pecaId;
    private Double desconto;
}