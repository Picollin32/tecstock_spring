package tecstock_spring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.envers.Audited;
import tecstock_spring.util.AuditListener;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "conta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
@Filter(name = "empresaFilter", condition = "empresa_id = :empresaId")
@EntityListeners(AuditListener.class)
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

    @Column(name = "acrescimo")
    private Double acrescimo;

    @Column(name = "desconto")
    private Double desconto;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_financeira_id")
    private CategoriaFinanceira categoriaFinanceira;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ContaParcela> parcelas = new ArrayList<>();

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
    @JsonProperty("categoriaId")
    public Long getCategoriaId() {
        return categoriaFinanceira != null ? categoriaFinanceira.getId() : null;
    }

    @Transient
    @JsonProperty("categoriaNome")
    public String getCategoriaNome() {
        return categoriaFinanceira != null ? categoriaFinanceira.getNome() : null;
    }

    @Transient
    @JsonProperty("fornecedorId")
    public Long getFornecedorId() {
        return fornecedor != null ? fornecedor.getId() : null;
    }

    @Transient
    @JsonProperty("fornecedorNome")
    public String getFornecedorNome() {
        return fornecedor != null ? fornecedor.getNome() : null;
    }
}
