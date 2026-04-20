package tecstock_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tecstock_spring.model.Conta;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContaComParcelasDTO {
    private Conta conta;
    private List<ContaParcelaDTO> parcelas;
}
