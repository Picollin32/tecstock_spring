package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tecstock_spring.model.Veiculo;
import tecstock_spring.model.Marca;
import java.util.List;
import java.util.Optional;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    Optional<Veiculo> findByPlaca(String placa);
    boolean existsByPlaca(String placa);
    boolean existsByPlacaAndIdNot(String placa, Long id);
    boolean existsByMarca(Marca marca);
    List<Veiculo> findByEmpresaId(Long empresaId);
    Optional<Veiculo> findByIdAndEmpresaId(Long id, Long empresaId);
    Optional<Veiculo> findByPlacaAndEmpresaId(String placa, Long empresaId);
    boolean existsByPlacaAndEmpresaId(String placa, Long empresaId);
    boolean existsByPlacaAndIdNotAndEmpresaId(String placa, Long id, Long empresaId);
}
