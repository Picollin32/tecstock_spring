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
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@Audited
@EntityListeners(AuditListener.class)
public class ServicoOrdemServico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;

    @Column(name = "numero_os", nullable = false)
    private String numeroOS;

    @Column(nullable = false)
    private Double valor;

    @Column(name = "categoria_veiculo")
    private String categoriaVeiculo;

    @Column(name = "data_realizacao", nullable = false)
    private LocalDateTime dataRealizacao;

    private String observacoes;

    @PrePersist
    protected void onCreate() {
        if (this.dataRealizacao == null) {
            this.dataRealizacao = LocalDateTime.now();
        }
    }
}
