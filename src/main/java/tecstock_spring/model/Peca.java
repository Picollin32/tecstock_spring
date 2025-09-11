package tecstock_spring.model;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Peca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String codigoFabricante;
    private double precoUnitario;
    private double precoFinal;
    private int quantidadeEstoque;

    @ManyToOne
    @JoinColumn(name = "fabricante_id")
    private Fabricante fabricante;

    @ManyToOne
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        calcularPrecoFinal();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calcularPrecoFinal();
    }
    
    public void calcularPrecoFinal() {
        if (fornecedor != null && fornecedor.getMargemLucro() != null) {
            double margemLucro = fornecedor.getMargemLucro();
            double margemDecimal = margemLucro > 1 ? margemLucro / 100 : margemLucro;
            this.precoFinal = precoUnitario * (1 + margemDecimal);
        } else {
            this.precoFinal = precoUnitario;
        }
    }

    public double getPrecoFinal() {
        if (precoFinal == 0 && precoUnitario > 0) {
            calcularPrecoFinal();
        }
        return precoFinal;
    }
}