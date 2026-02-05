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
    private String nomeCompleto;
    private Integer nivelAcesso;
    private String token;
    private ConsultorDTO consultor;
    private EmpresaDTO empresa;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ConsultorDTO {
        private Long id;
        private String nome;
        private Integer nivelAcesso;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmpresaDTO {
        private Long id;
        private String nomeFantasia;
    }
}
