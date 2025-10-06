package tecstock_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    private Long id;
    private String nomeUsuario;
    private String nomeCompleto; // Nome do consultor/funcionário
    private Integer nivelAcesso; // Vem do funcionário associado
    private String token; // Pode ser usado no futuro para JWT
    private ConsultorDTO consultor; // Dados do consultor (funcionário) associado ao usuário
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ConsultorDTO {
        private Long id;
        private String nome;
        private Integer nivelAcesso;
    }
}
