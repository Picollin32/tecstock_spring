package tecstock_spring.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Checklist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @Column(length = 1000)
    private String queixaPrincipal;
    private Integer nivelCombustivel;
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
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
