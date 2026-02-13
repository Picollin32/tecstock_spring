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
public class VeiculoPesquisaDTO {
    private Long id;
    private String nome;
    private String placa;
    private String modelo;
    private Integer ano;
    private String categoria;
    private String cor;
    private Double quilometragem;
    private MarcaSimplificadaDTO marca;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public VeiculoPesquisaDTO(Long id, String nome, String placa, String modelo, Integer ano, 
                              String categoria, String cor, Double quilometragem,
                              Long marcaId, String marcaNome, 
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nome = nome;
        this.placa = placa;
        this.modelo = modelo;
        this.ano = ano;
        this.categoria = categoria;
        this.cor = cor;
        this.quilometragem = quilometragem;
        this.marca = (marcaId != null) ? new MarcaSimplificadaDTO(marcaId, marcaNome) : null;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MarcaSimplificadaDTO {
        private Long id;
        private String marca;
    }
}
