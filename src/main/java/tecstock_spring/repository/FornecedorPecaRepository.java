package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tecstock_spring.model.FornecedorPeca;
import tecstock_spring.model.FornecedorPecaId;

public interface FornecedorPecaRepository extends JpaRepository<FornecedorPeca, FornecedorPecaId> {
}