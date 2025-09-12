package tecstock_spring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdemServico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroOS;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private String clienteNome;
    
    @Column(nullable = false)
    private String clienteCpf;
    
    private String clienteTelefone;
    private String clienteEmail;

    @Column(nullable = false)
    private String veiculoNome;
    
    private String veiculoMarca;
    private String veiculoAno;
    private String veiculoCor;
    
    @Column(nullable = false)
    private String veiculoPlaca;
    
    private String veiculoQuilometragem;
    private String veiculoCategoria;

    private Long checklistId;

    @Column(length = 1000)
    private String queixaPrincipal;

    private String nomeMecanico;
    private String nomeConsultor;

    private Integer numeroParcelas;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "ordem_servico_servicos",
        joinColumns = @JoinColumn(name = "ordem_servico_id"),
        inverseJoinColumns = @JoinColumn(name = "servico_id")
    )
    private List<Servico> servicosRealizados;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "ordem_servico_id")
    private List<PecaOrdemServico> pecasUtilizadas;

    @Column(name = "preco_total")
    private Double precoTotal;
    
    @Column(name = "preco_total_servicos")
    private Double precoTotalServicos;
    
    @Column(name = "preco_total_pecas")
    private Double precoTotalPecas;

    @Column(nullable = false)
    @Builder.Default
    private Integer garantiaMeses = 3;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_pagamento_id")
    private TipoPagamento tipoPagamento;

    @Column(length = 2000)
    private String observacoes;

    @Column(nullable = false)
    @Builder.Default
    private String status = "Pendente";

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.dataHora == null) {
            this.dataHora = LocalDateTime.now();
        }
        if (this.garantiaMeses == null) {
            this.garantiaMeses = 3;
        }
        if (this.status == null) {
            this.status = "Pendente";
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Calcula o valor total dos serviços baseado na categoria do veículo
     */
    public Double calcularPrecoTotalServicos() {
        if (this.servicosRealizados == null || this.servicosRealizados.isEmpty()) {
            return 0.0;
        }
        
        return this.servicosRealizados.stream()
            .filter(servico -> servico != null)
            .mapToDouble(servico -> servico.precoParaCategoria(this.veiculoCategoria))
            .sum();
    }
    
    /**
     * Calcula o valor total das peças
     */
    public Double calcularPrecoTotalPecas() {
        if (this.pecasUtilizadas == null || this.pecasUtilizadas.isEmpty()) {
            return 0.0;
        }
        
        return this.pecasUtilizadas.stream()
            .filter(peca -> peca != null && peca.getValorTotal() != null)
            .mapToDouble(PecaOrdemServico::getValorTotal)
            .sum();
    }
    
    /**
     * Calcula e atualiza todos os valores (serviços, peças e total geral)
     */
    public void calcularTodosOsPrecos() {
        this.precoTotalServicos = calcularPrecoTotalServicos();
        this.precoTotalPecas = calcularPrecoTotalPecas();
        this.precoTotal = this.precoTotalServicos + this.precoTotalPecas;
    }
}
