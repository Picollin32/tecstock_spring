package tecstock_spring.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
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
public class Fornecedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nome;
    
    @Column(unique = true)
    private String cnpj;
    
    private String telefone;
    
    @Email(message = "Email inválido")
    private String email;
    
    @Column(precision = 10, scale = 4)
    private BigDecimal margemLucro;
    
    private String rua;
    private String numeroCasa;
    private String bairro;
    private String cidade;
    private String uf;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        updatedAt = null;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}