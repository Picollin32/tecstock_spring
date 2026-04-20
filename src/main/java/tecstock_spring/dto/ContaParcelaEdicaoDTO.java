package tecstock_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaParcelaEdicaoDTO {
    private Double valor;
    private LocalDate dataVencimento;
}
