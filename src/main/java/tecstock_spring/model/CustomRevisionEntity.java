package tecstock_spring.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@Entity
@Table(name = "revinfo")
@RevisionEntity(CustomRevisionListener.class)
@Data
public class CustomRevisionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    private Long id;
    
    @RevisionTimestamp
    private Long timestamp;
    
    @Column(name = "usuario")
    private String usuario;
}
