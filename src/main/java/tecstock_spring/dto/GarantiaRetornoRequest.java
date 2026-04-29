package tecstock_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GarantiaRetornoRequest {

    private Long servicoId;
    private String motivo;
}
