package tecstock_spring.model;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Column;
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
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static final String CATEGORIA_CAMINHONETE = "caminhonete";
    public static final String CATEGORIA_PASSEIO = "passeio";

    public Double precoParaCategoria(String categoria) {
        if (categoria == null) return precoPasseio;
        if (CATEGORIA_CAMINHONETE.equalsIgnoreCase(categoria)) {
            return precoCaminhonete != null ? precoCaminhonete : precoPasseio;
        }
        return precoPasseio;
    }
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}