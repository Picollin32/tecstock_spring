package tecstock_spring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "conta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(nullable = false, length = 255)
    private String descricao;

    @Column(nullable = false)
    private Double valor;

    @Column(name = "mes_referencia", nullable = false)
    private Integer mesReferencia;

    @Column(name = "ano_referencia", nullable = false)
    private Integer anoReferencia;

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @Column(nullable = false)
    @Builder.Default
    private Boolean pago = false;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Column(name = "ordem_servico_id")
    private Long ordemServicoId;

    @Column(name = "ordem_servico_numero", length = 50)
    private String ordemServicoNumero;

    @Column(name = "parcela_numero")
    private Integer parcelaNumero;

    @Column(name = "total_parcelas")
    private Integer totalParcelas;

    @Column(name = "origem_tipo", length = 20)
    private String origemTipo;

    @Column(name = "fiado_grupo_id", length = 36)
    private String fiadoGrupoId;

    @Column(name = "valor_pago_parcial")
    @Builder.Default
    private Double valorPagoParcial = 0.0;

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
}
