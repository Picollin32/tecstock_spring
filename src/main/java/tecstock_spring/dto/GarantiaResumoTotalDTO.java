package tecstock_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GarantiaResumoTotalDTO {

    private Integer total;
    private Integer ativas;
    private Integer reclamadas;
    private Integer expiradas;
}
