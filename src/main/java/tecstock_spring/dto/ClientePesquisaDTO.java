package tecstock_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.sql.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientePesquisaDTO {
    private Long id;
    private String nome;
    private String cpf;
    private String telefone;
    private String email;
    private Date dataNascimento;
    private String rua;
    private String numeroCasa;
    private String bairro;
    private String cidade;
    private String uf;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
