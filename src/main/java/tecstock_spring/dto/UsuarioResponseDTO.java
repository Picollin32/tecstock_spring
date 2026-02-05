package tecstock_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tecstock_spring.model.Funcionario;
import tecstock_spring.model.Empresa;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {
    private Long id;
    private String nomeUsuario;
    private Integer nivelAcesso;
    private Funcionario consultor;
    private Empresa empresa;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
