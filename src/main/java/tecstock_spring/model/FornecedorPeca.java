package tecstock_spring.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FornecedorPeca {

    @EmbeddedId
    private FornecedorPecaId id;

    @ManyToOne
    @MapsId("fornecedorId")
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;

    @ManyToOne
    @MapsId("pecaId")
    @JoinColumn(name = "peca_id")
    private Peca peca;

    private double desconto;
}