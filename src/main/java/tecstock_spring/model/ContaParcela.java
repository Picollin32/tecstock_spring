package tecstock_spring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "conta_parcela")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
@AuditTable("conta_parcela_aud")
public class ContaParcela {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conta", nullable = false)
    private Conta conta;

    @Column(name = "parcela_numero", nullable = false)
    private Integer parcelaNumero;

    @Column(name = "total_parcelas", nullable = false)
    private Integer totalParcelas;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(nullable = false)
    private Double valor;

    @Column(nullable = false)
    @Builder.Default
    private Boolean pago = false;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Column(name = "acrescimo")
    private Double acrescimo;

    @Column(name = "desconto")
    private Double desconto;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.pago == null) {
            this.pago = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Transient
    @JsonProperty("contaId")
    public Long getContaId() {
        return conta != null ? conta.getId() : null;
    }
}
