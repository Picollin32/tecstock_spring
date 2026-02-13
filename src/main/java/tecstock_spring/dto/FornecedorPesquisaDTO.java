package tecstock_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FornecedorPesquisaDTO {
    private Long id;
    private String nome;
    private String cnpj;
    private String telefone;
    private String email;
    private BigDecimal margemLucro;
    private String rua;
    private String numeroCasa;
    private String bairro;
    private String cidade;
    private String uf;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
