package tecstock_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tecstock_spring.model.Funcionario;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {
    private Long id;
    private String nomeUsuario;
    private String senha;
    private Funcionario consultor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
