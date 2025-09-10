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
    private Double valorUnitario; // Preço da peça no momento da venda
    
    @Column(name = "valor_total")
    private Double valorTotal; // quantidade * valorUnitario
    
    @PrePersist
    @PreUpdate
    protected void calculateValues() {
        if (peca != null) {
            this.valorUnitario = peca.getPrecoUnitario();
            this.valorTotal = this.valorUnitario * this.quantidade;
        }
    }
}
