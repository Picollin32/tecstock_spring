package tecstock_spring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "peca_ordem_servico")
public class PecaOrdemServico {
    
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
    protected void calculateValuesOnCreate() {
        if (peca != null) {
            if (this.valorUnitario == null) {
                this.valorUnitario = peca.getPrecoFinal();
            }
            if (this.valorTotal == null) {
                this.valorTotal = this.valorUnitario * this.quantidade;
            }
        }
    }
    
    @PreUpdate  
    protected void recalculateOnUpdate() {
        if (this.valorUnitario != null && this.quantidade != null) {
            this.valorTotal = this.valorUnitario * this.quantidade;
        }
    }
}
