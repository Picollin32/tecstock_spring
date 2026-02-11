package tecstock_spring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Filter(name = "empresaFilter", condition = "empresa_id = :empresaId")
public class AuditoriaLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;
    
    @Column(nullable = false)
    private String entidade;
    
    @Column(nullable = false)
    private Long entidadeId;
    
    @Column(nullable = false)
    private String operacao;
    
    @Column(nullable = false)
    private String usuario;
    
    @Column(nullable = false)
    private LocalDateTime dataHora;
    
    @Column(columnDefinition = "TEXT")
    private String valoresAntigos;
    
    @Column(columnDefinition = "TEXT")
    private String valoresNovos;
    
    @Column(columnDefinition = "TEXT")
    private String descricao;
}
