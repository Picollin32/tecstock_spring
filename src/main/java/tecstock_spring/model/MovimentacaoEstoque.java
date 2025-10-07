package tecstock_spring.model;

import java.time.LocalDateTime;
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
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
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
public class MovimentacaoEstoque {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String codigoPeca;

    @ManyToOne
    @JoinColumn(name = "fornecedor_id", nullable = false)
    private Fornecedor fornecedor;

    @Column(nullable = false)
    private int quantidade;

    @Column(name = "numero_nota_fiscal", nullable = false)
    private String numeroNotaFiscal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimentacao tipoMovimentacao;

    @Column(name = "data_entrada")
    private LocalDateTime dataEntrada;

    @Column(name = "data_saida")
    private LocalDateTime dataSaida;

    private String observacoes;
    
    @Column(name = "preco_unitario")
    private Double precoUnitario;
    
    @Column(name = "preco_final")
    private Double precoFinal;

    public enum TipoMovimentacao {
        ENTRADA,
        SAIDA
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime agora = LocalDateTime.now();
        
        if (tipoMovimentacao == TipoMovimentacao.ENTRADA) {
            if (dataEntrada == null) {
                dataEntrada = agora;
            }
        }

        else if (tipoMovimentacao == TipoMovimentacao.SAIDA) {
            if (dataSaida == null) {
                dataSaida = agora;
            }
        }
        calcularPrecoFinal();
    }

    @PreUpdate
    protected void onUpdate() {
        calcularPrecoFinal();
    }

    public void calcularPrecoFinal() {
        if (precoUnitario == null) {
            precoFinal = null;
            return;
        }

        if (fornecedor != null && fornecedor.getMargemLucro() != null) {
            double margemLucro = fornecedor.getMargemLucro();
            double margemDecimal = margemLucro > 1 ? margemLucro / 100.0 : margemLucro;
            this.precoFinal = this.precoUnitario * (1 + margemDecimal);
        } else {
            this.precoFinal = this.precoUnitario;
        }
    }
}
