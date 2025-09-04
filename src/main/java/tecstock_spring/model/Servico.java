package tecstock_spring.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Servico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Double precoPasseio;
    private Double precoCaminhonete;

    public static final String CATEGORIA_CAMINHONETE = "caminhonete";
    public static final String CATEGORIA_PASSEIO = "passeio";

    public Double precoParaCategoria(String categoria) {
        if (categoria == null) return precoPasseio;
        if (CATEGORIA_CAMINHONETE.equalsIgnoreCase(categoria)) {
            return precoCaminhonete != null ? precoCaminhonete : precoPasseio;
        }
        return precoPasseio;
    }
}