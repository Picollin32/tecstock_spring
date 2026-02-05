package tecstock_spring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import tecstock_spring.util.AuditListener;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
@EntityListeners(AuditListener.class)
public class Checklist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    private int numeroChecklist;
    private String data;
    private String hora;
    private String clienteNome;
    private String clienteCpf;
    private String clienteTelefone;
    private String clienteEmail;
    private String veiculoNome;
    private String veiculoMarca;
    private String veiculoAno;
    private String veiculoCor;
    private String veiculoPlaca;
    private String veiculoQuilometragem;
    private String veiculoCategoria;
    @Column(length = 1000)
    private String queixaPrincipal;
    private Integer nivelCombustivel;
    
    @ManyToOne
    @JoinColumn(name = "consultor_id")
    private Funcionario consultor;
    private String parachoquesDianteiro;
    private String parachoquesTraseiro;
    private String capo;
    private String portaMalas;
    private String portaDiantEsq;
    private String portaTrasEsq;
    private String portaDiantDir;
    private String portaTrasDir;
    private String teto;
    private String paraBrisa;
    private String retrovisores;
    private String pneusRodas;
    private String estepe;

    @Column(length = 500)
    private String parachoquesDianteiroObs;
    @Column(length = 500)
    private String parachoquesTraseiroObs;
    @Column(length = 500)
    private String capoObs;
    @Column(length = 500)
    private String portaMalasObs;
    @Column(length = 500)
    private String portaDiantEsqObs;
    @Column(length = 500)
    private String portaTrasEsqObs;
    @Column(length = 500)
    private String portaDiantDirObs;
    @Column(length = 500)
    private String portaTrasDirObs;
    @Column(length = 500)
    private String tetoObs;
    @Column(length = 500)
    private String paraBrisaObs;
    @Column(length = 500)
    private String retrovisoresObs;
    @Column(length = 500)
    private String pneusRodasObs;
    @Column(length = 500)
    
    private String estepeObs;
    private String buzina;
    private String farolBaixoAlto;
    private String setasPiscaAlerta;
    private String luzFreio;
    private String limpadorParaBrisa;
    private String arCondicionado;
    private String radioMultimidia;
    private String manualLivreto;
    private String crlv;
    private String chaveReserva;
    private String macaco;
    private String chaveRoda;
    private String triangulo;
    private String tapetes;
    
    @Column(nullable = false)
    @Builder.Default
    private String status = "Aberto";
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }

        this.updatedAt = null;
    }
    
    @PreUpdate
    protected void onUpdate() {

        this.updatedAt = LocalDateTime.now();
    }
}
