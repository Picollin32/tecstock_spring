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
public class PecaPesquisaDTO {
    private Long id;
    private String nome;
    private String codigoFabricante;
    private Double precoUnitario;
    private Double precoFinal;
    private Integer quantidadeEstoque;
    private Integer estoqueSeguranca;
    private Integer unidadesUsadasEmOS;
    private FabricanteSimplificadoDTO fabricante;
    private FornecedorSimplificadoDTO fornecedor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PecaPesquisaDTO(Long id, String nome, String codigoFabricante, Double precoUnitario, 
                           Double precoFinal, Integer quantidadeEstoque, Integer estoqueSeguranca,
                           Integer unidadesUsadasEmOS, Long fabricanteId, String fabricanteNome,
                           Long fornecedorId, String fornecedorNome, String fornecedorCnpj,
                           String fornecedorTelefone, String fornecedorEmail, BigDecimal fornecedorMargemLucro,
                           String fornecedorRua, String fornecedorNumeroCasa, String fornecedorBairro,
                           String fornecedorCidade, String fornecedorUf,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nome = nome;
        this.codigoFabricante = codigoFabricante;
        this.precoUnitario = precoUnitario;
        this.precoFinal = precoFinal;
        this.quantidadeEstoque = quantidadeEstoque;
        this.estoqueSeguranca = estoqueSeguranca;
        this.unidadesUsadasEmOS = unidadesUsadasEmOS;
        this.fabricante = (fabricanteId != null) ? new FabricanteSimplificadoDTO(fabricanteId, fabricanteNome) : null;
        this.fornecedor = (fornecedorId != null) ? new FornecedorSimplificadoDTO(fornecedorId, fornecedorNome, 
            fornecedorCnpj, fornecedorTelefone, fornecedorEmail, fornecedorMargemLucro,
            fornecedorRua, fornecedorNumeroCasa, fornecedorBairro, fornecedorCidade, fornecedorUf) : null;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FabricanteSimplificadoDTO {
        private Long id;
        private String nome;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FornecedorSimplificadoDTO {
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
    }
}
