package tecstock_spring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String entidade; // Nome da entidade (Cliente, Veiculo, etc)
    
    @Column(nullable = false)
    private Long entidadeId; // ID da entidade afetada
    
    @Column(nullable = false)
    private String operacao; // CREATE, UPDATE, DELETE
    
    @Column(nullable = false)
    private String usuario; // Email/username do usuário que fez a alteração
    
    @Column(nullable = false)
    private LocalDateTime dataHora;
    
    @Column(columnDefinition = "TEXT")
    private String valoresAntigos; // JSON com valores antes da alteração
    
    @Column(columnDefinition = "TEXT")
    private String valoresNovos; // JSON com valores depois da alteração
    
    @Column(columnDefinition = "TEXT")
    private String descricao; // Descrição amigável da alteração
}
