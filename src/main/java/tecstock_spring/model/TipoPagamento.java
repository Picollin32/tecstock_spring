package tecstock_spring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import tecstock_spring.util.AuditListener;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"codigo", "empresa_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
@Filter(name = "empresaFilter", condition = "empresa_id = :empresaId")
@EntityListeners(AuditListener.class)
public class TipoPagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false)
    private Integer codigo;
    
    @Column(name = "id_forma_pagamento")
    private Integer idFormaPagamento;

    @Column(name = "quantidade_parcelas")
    private Integer quantidadeParcelas;

    @Column(name = "dias_entre_parcelas")
    private Integer diasEntreParcelas;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        if (quantidadeParcelas == null || quantidadeParcelas < 1) {
            quantidadeParcelas = 1;
        }
        if (diasEntreParcelas == null || diasEntreParcelas < 0) {
            diasEntreParcelas = 0;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = null;
    }
    
    @PreUpdate
    protected void onUpdate() {
        if (quantidadeParcelas == null || quantidadeParcelas < 1) {
            quantidadeParcelas = 1;
        }
        if (diasEntreParcelas == null || diasEntreParcelas < 0) {
            diasEntreParcelas = 0;
        }
        updatedAt = LocalDateTime.now();
    }
}
