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
public class FabricantePesquisaDTO {
    private Long id;
    private String nome;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
