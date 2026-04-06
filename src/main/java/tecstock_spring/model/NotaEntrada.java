package tecstock_spring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.envers.Audited;
import tecstock_spring.util.AuditListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "nota_entrada",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_nota_entrada_empresa_fornecedor_numero", columnNames = {
                        "empresa_id", "fornecedor_id", "numero_nota_fiscal"
                })
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
@Filter(name = "empresaFilter", condition = "empresa_id = :empresaId")
@EntityListeners(AuditListener.class)
public class NotaEntrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fornecedor_id", nullable = false)
    private Fornecedor fornecedor;

    @Column(name = "numero_nota_fiscal", nullable = false, length = 80)
    private String numeroNotaFiscal;

    @Column(name = "data_entrada", nullable = false)
    private LocalDateTime dataEntrada;

    @Column(name = "valor_total_compra", nullable = false)
    private Double valorTotalCompra;

    @Column(name = "valor_total_frete")
    private Double valorTotalFrete;

    @Column(name = "forma_pagamento", length = 30)
    private String formaPagamento;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.dataEntrada == null) {
            this.dataEntrada = LocalDateTime.now();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
