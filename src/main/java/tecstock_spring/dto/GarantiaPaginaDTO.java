package tecstock_spring.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GarantiaPaginaDTO {

    private List<GarantiaResumoDTO> content;
    private Integer totalElements;
    private Integer totalPages;
    private Integer number;
    private GarantiaResumoTotalDTO resumo;
}
