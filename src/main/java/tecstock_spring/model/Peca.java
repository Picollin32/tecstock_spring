package tecstock_spring.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import org.hibernate.envers.Audited;
import tecstock_spring.util.AuditListener;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Audited
@EntityListeners(AuditListener.class)
public class Peca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String codigoFabricante;
    private double precoUnitario;
    private double precoFinal;
    private int quantidadeEstoque;
    private int estoqueSeguranca;
    
    @Column(name = "unidades_usadas_em_os")
    private Integer unidadesUsadasEmOS = 0;

    @ManyToOne
    @JoinColumn(name = "fabricante_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Fabricante fabricante;

    @ManyToOne
    @JoinColumn(name = "fornecedor_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
            BigDecimal margemLucro = fornecedor.getMargemLucro();
            // Se a margem for maior que 1, assume que está em percentual e divide por 100
            BigDecimal margemDecimal = margemLucro.compareTo(BigDecimal.ONE) > 0 
                ? margemLucro.divide(new BigDecimal("100"), 4, java.math.RoundingMode.HALF_UP) 
                : margemLucro;
            
            // Converte precoUnitario para BigDecimal para cálculo preciso
            BigDecimal precoUnitarioBD = BigDecimal.valueOf(precoUnitario);
            // Calcula: precoUnitario * (1 + margemDecimal)
            BigDecimal um = BigDecimal.ONE;
            BigDecimal multiplicador = um.add(margemDecimal);
            BigDecimal precoFinalBD = precoUnitarioBD.multiply(multiplicador);
            
            // Arredonda para 2 casas decimais e converte para double
            this.precoFinal = precoFinalBD.setScale(2, java.math.RoundingMode.HALF_UP).doubleValue();
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