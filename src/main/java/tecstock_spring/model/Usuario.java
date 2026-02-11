package tecstock_spring.model;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.annotations.Filter;
import tecstock_spring.util.AuditListener;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
@Filter(name = "empresaFilter", condition = "empresa_id = :empresaId")
@EntityListeners(AuditListener.class)
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome de usuário é obrigatório")
    @Size(min = 3, max = 50, message = "Nome de usuário deve ter entre 3 e 50 caracteres")
    @Column(unique = true, nullable = false)
    private String nomeUsuario;
    
    @Column(nullable = false)
    private String senha;
    
    private String nomeCompleto;
    
    @NotNull(message = "Nível de acesso é obrigatório")
    @Column(nullable = false)
    private Integer nivelAcesso;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "consultor_id", nullable = true)
    private Funcionario consultor;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "empresa_id", nullable = true)
    private Empresa empresa;
    
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
