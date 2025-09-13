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
public class Orcamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroOrcamento;

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

    @Column(length = 1000)
    private String queixaPrincipal;

    private String nomeMecanico;
    private String nomeConsultor;

    private Integer numeroParcelas;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "orcamento_servicos",
        joinColumns = @JoinColumn(name = "orcamento_id"),
        inverseJoinColumns = @JoinColumn(name = "servico_id")
    )
    private List<Servico> servicosOrcados;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "orcamento_id")
    private List<PecaOrcamento> pecasOrcadas;

    @Column(name = "preco_total")
    private Double precoTotal;
    
    @Column(name = "preco_total_servicos")
    private Double precoTotalServicos;
    
    @Column(name = "preco_total_pecas")
    private Double precoTotalPecas;

    @Column(name = "desconto_servicos")
    private Double descontoServicos;
    
    @Column(name = "desconto_pecas")
    private Double descontoPecas;

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
    private String status = "ABERTO";

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
            this.status = "ABERTO";
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
        if (this.servicosOrcados == null || this.servicosOrcados.isEmpty()) {
            return 0.0;
        }
        
        return this.servicosOrcados.stream()
            .filter(servico -> servico != null)
            .mapToDouble(servico -> servico.precoParaCategoria(this.veiculoCategoria))
            .sum();
    }
    
    /**
     * Calcula o valor total das peças
     */
    public Double calcularPrecoTotalPecas() {
        if (this.pecasOrcadas == null || this.pecasOrcadas.isEmpty()) {
            return 0.0;
        }
        
        return this.pecasOrcadas.stream()
            .filter(peca -> peca != null && peca.getValorTotal() != null)
            .mapToDouble(PecaOrcamento::getValorTotal)
            .sum();
    }
    
    /**
     * Calcula e atualiza todos os valores (serviços, peças e total geral)
     */
    public void calcularTodosOsPrecos() {
        this.precoTotalServicos = calcularPrecoTotalServicos();
        this.precoTotalPecas = calcularPrecoTotalPecas();
        
        // Aplicar descontos se existirem
        double totalServicosComDesconto = this.precoTotalServicos - (this.descontoServicos != null ? this.descontoServicos : 0.0);
        double totalPecasComDesconto = this.precoTotalPecas - (this.descontoPecas != null ? this.descontoPecas : 0.0);
        
        this.precoTotal = totalServicosComDesconto + totalPecasComDesconto;
    }
    
    /**
     * Calcula o máximo de desconto permitido para serviços (10%)
     */
    public Double calcularMaxDescontoServicos() {
        if (this.precoTotalServicos == null) {
            return 0.0;
        }
        return this.precoTotalServicos * 0.10;
    }
    
    /**
     * Calcula o máximo de desconto permitido para peças (baseado na margem de lucro)
     */
    public Double calcularMaxDescontoPecas() {
        if (this.pecasOrcadas == null || this.pecasOrcadas.isEmpty()) {
            return 0.0;
        }
        
        return this.pecasOrcadas.stream()
            .filter(pecaOrcamento -> pecaOrcamento != null && pecaOrcamento.getPeca() != null)
            .mapToDouble(pecaOrcamento -> {
                Peca peca = pecaOrcamento.getPeca();
                // Margem de lucro = precoFinal - precoUnitario
                double margemPorUnidade = peca.getPrecoFinal() - peca.getPrecoUnitario();
                return margemPorUnidade * pecaOrcamento.getQuantidade();
            })
            .sum();
    }
    
    /**
     * Valida se o desconto de serviços está dentro do limite permitido
     */
    public boolean isDescontoServicosValido(Double desconto) {
        if (desconto == null || desconto <= 0) {
            return true;
        }
        return desconto <= calcularMaxDescontoServicos();
    }
    
    /**
     * Valida se o desconto de peças está dentro do limite permitido
     */
    public boolean isDescontoPecasValido(Double desconto) {
        if (desconto == null || desconto <= 0) {
            return true;
        }
        return desconto <= calcularMaxDescontoPecas();
    }
}