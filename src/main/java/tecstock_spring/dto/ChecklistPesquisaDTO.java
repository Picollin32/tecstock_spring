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
public class ChecklistPesquisaDTO {
    private Long id;
    private Integer numeroChecklist;
    private LocalDateTime createdAt;
    private String clienteNome;
    private String clienteCpf;
    private String veiculoNome;
    private String veiculoPlaca;
    private String status;
    private String consultorNome;
}
