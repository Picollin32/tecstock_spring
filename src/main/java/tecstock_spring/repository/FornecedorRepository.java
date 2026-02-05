package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tecstock_spring.model.Fornecedor;
import java.util.List;
import java.util.Optional;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    Optional<Fornecedor> findByCnpj(String cnpj);
    boolean existsByCnpj(String cnpj);
    boolean existsByCnpjAndIdNot(String cnpj, Long id);

    List<Fornecedor> findByEmpresaId(Long empresaId);
    Optional<Fornecedor> findByIdAndEmpresaId(Long id, Long empresaId);
    Optional<Fornecedor> findByCnpjAndEmpresaId(String cnpj, Long empresaId);
    boolean existsByCnpjAndEmpresaId(String cnpj, Long empresaId);
    boolean existsByCnpjAndIdNotAndEmpresaId(String cnpj, Long id, Long empresaId);
}
