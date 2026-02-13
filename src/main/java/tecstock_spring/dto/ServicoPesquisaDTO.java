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
public class ServicoPesquisaDTO {
    private Long id;
    private String nome;
    private Double precoPasseio;
    private Double precoCaminhonete;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
