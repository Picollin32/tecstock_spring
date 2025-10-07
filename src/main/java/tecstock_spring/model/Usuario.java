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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
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
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nomeUsuario;
    
    @Column(nullable = false)
    private String senha;
    
    private String nomeCompleto; // Nome completo do usuário (usado quando não tem consultor vinculado)
    
    @Column(nullable = false)
    private Integer nivelAcesso; // 0 = Admin, 1 = Consultor
    
    @ManyToOne
    @JoinColumn(name = "consultor_id", nullable = true) // Opcional - admin não precisa de consultor
    private Funcionario consultor;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
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
