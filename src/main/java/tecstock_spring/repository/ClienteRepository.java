package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tecstock_spring.model.Cliente;
import tecstock_spring.model.Empresa;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
    boolean existsByCpfAndIdNot(String cpf, Long id);

    List<Cliente> findByEmpresa(Empresa empresa);
    List<Cliente> findByEmpresaId(Long empresaId);
    Optional<Cliente> findByIdAndEmpresaId(Long id, Long empresaId);
    boolean existsByCpfAndEmpresaId(String cpf, Long empresaId);
    boolean existsByCpfAndIdNotAndEmpresaId(String cpf, Long id, Long empresaId);
}
