package tecstock_spring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import tecstock_spring.util.AuditListener;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Audited
@EntityListeners(AuditListener.class)
@Table(name = "peca_orcamento")
public class PecaOrcamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "peca_id", nullable = false)
    private Peca peca;
    
    @Column(nullable = false)
    private Integer quantidade;
    
    @Column(name = "valor_unitario")
    private Double valorUnitario;
    
    @Column(name = "valor_total")
    private Double valorTotal;
    
    @PrePersist
    @PreUpdate
    protected void calculateValues() {
        if (peca != null) {
            this.valorUnitario = peca.getPrecoFinal();
            this.valorTotal = this.valorUnitario * this.quantidade;
        }
    }
}