package tecstock_spring.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import tecstock_spring.util.AuditListener;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Audited
@EntityListeners(AuditListener.class)
public class Empresa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 dígitos")
    @Column(unique = true, nullable = false, length = 14)
    private String cnpj;
    
    @NotBlank(message = "Razão Social é obrigatória")
    @Column(nullable = false)
    private String razaoSocial;
    
    @Column(nullable = false)
    private String nomeFantasia;
    
    @Pattern(regexp = "\\d{9}", message = "Inscrição Estadual deve conter 9 dígitos")
    @Column(length = 9)
    private String inscricaoEstadual;
    
    @Column(length = 20)
    private String inscricaoMunicipal;

    @Column(length = 15)
    private String telefone;
    
    @Column(length = 100)
    private String email;
    
    @Column(length = 100)
    private String site;
   
    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{8}", message = "CEP deve conter 8 dígitos")
    @Column(nullable = false, length = 8)
    private String cep;
    
    @NotBlank(message = "Logradouro é obrigatório")
    @Column(nullable = false)
    private String logradouro;
    
    @NotBlank(message = "Número é obrigatório")
    @Column(nullable = false, length = 10)
    private String numero;
    
    @Column(length = 100)
    private String complemento;
    
    @NotBlank(message = "Bairro é obrigatório")
    @Column(nullable = false)
    private String bairro;
    
    @NotBlank(message = "Cidade é obrigatória")
    @Column(nullable = false)
    private String cidade;
    
    @NotBlank(message = "UF é obrigatória")
    @Column(nullable = false, length = 2)
    private String uf;
    
    @Pattern(regexp = "\\d{7}", message = "Código do município deve conter 7 dígitos")
    @Column(length = 7)
    private String codigoMunicipio;

    @Column(length = 1)
    private String regimeTributario;
    
    @Column(length = 15)
    private String cnae;

    @Column(nullable = false)
    private Boolean ativa = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (ativa == null) {
            ativa = true;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
