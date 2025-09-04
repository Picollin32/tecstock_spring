package tecstock_spring.model;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FornecedorPecaId implements Serializable {
    private Long fornecedorId;
    private Long pecaId;
}