package tecstock_spring.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import tecstock_spring.util.AuditListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Audited
@EntityListeners(AuditListener.class)
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    private LocalDate data;

    @Column(name = "hora_inicio")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaInicio;

    @Column(name = "hora_fim")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaFim;

    private String placaVeiculo;
    private String nomeMecanico;
    private String nomeConsultor;
    private String cor;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        updatedAt = null;
    }
    
    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
    }
}