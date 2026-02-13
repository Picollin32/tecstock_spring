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
public class MarcaPesquisaDTO {
    private Long id;
    private String marca;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
