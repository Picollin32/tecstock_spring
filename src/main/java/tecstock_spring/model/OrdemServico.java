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
     * Se os valores já existem (OS já foi salva), preserva os valores históricos
     */
    public void calcularTodosOsPrecos() {
        // Só calcula valores de serviços se ainda não foram definidos (preserva valores históricos)
        if (this.precoTotalServicos == null) {
            this.precoTotalServicos = calcularPrecoTotalServicos();
        }
        
        // Só calcula valores de peças se ainda não foram definidos (preserva valores históricos)
        if (this.precoTotalPecas == null) {
            this.precoTotalPecas = calcularPrecoTotalPecas();
        }
        
        // Recalcula apenas o total geral aplicando descontos
        double totalServicosComDesconto = this.precoTotalServicos - (this.descontoServicos != null ? this.descontoServicos : 0.0);
        double totalPecasComDesconto = this.precoTotalPecas - (this.descontoPecas != null ? this.descontoPecas : 0.0);
        
        this.precoTotal = totalServicosComDesconto + totalPecasComDesconto;
    }
    
    /**
     * Força o recálculo de todos os valores (usado apenas na criação/edição explícita)
     */
    public void forcarRecalculoTodosOsPrecos() {
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
        if (this.pecasUtilizadas == null || this.pecasUtilizadas.isEmpty()) {
            return 0.0;
        }
        
        return this.pecasUtilizadas.stream()
            .filter(pecaOS -> pecaOS != null && pecaOS.getPeca() != null)
            .mapToDouble(pecaOS -> {
                Peca peca = pecaOS.getPeca();
                // Margem de lucro = precoFinal - precoUnitario
                double margemPorUnidade = peca.getPrecoFinal() - peca.getPrecoUnitario();
                return margemPorUnidade * pecaOS.getQuantidade();
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
