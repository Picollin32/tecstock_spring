package tecstock_spring.model;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import tecstock_spring.util.AuditListener;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
@EntityListeners(AuditListener.class)
public class Servico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Double precoPasseio;
    private Double precoCaminhonete;
    
    @Column(name = "unidades_usadas_em_os")
    @Builder.Default
    private Integer unidadesUsadasEmOS = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static final String CATEGORIA_CAMINHONETE = "Caminhonete";
    public static final String CATEGORIA_PASSEIO = "Passeio";

    public Double precoParaCategoria(String categoria) {
        if (categoria == null) return 0.0;
        
        if (CATEGORIA_CAMINHONETE.equalsIgnoreCase(categoria)) {
            return precoCaminhonete != null ? precoCaminhonete : 0.0;
        } else if (CATEGORIA_PASSEIO.equalsIgnoreCase(categoria)) {
            return precoPasseio != null ? precoPasseio : 0.0;
        }
        
        return 0.0;
    }
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        // Garante que updatedAt fica null na criação
        updatedAt = null;
    }
    
    @PreUpdate
    protected void onUpdate() {
        // Sempre atualiza o updatedAt quando editar
        updatedAt = LocalDateTime.now();
    }
}