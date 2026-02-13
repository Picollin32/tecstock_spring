package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tecstock_spring.dto.VeiculoPesquisaDTO;
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
    
    @org.springframework.data.jpa.repository.Query("SELECT new tecstock_spring.dto.VeiculoPesquisaDTO(v.id, v.nome, v.placa, v.modelo, v.ano, v.categoria, v.cor, v.quilometragem, v.marca.id, v.marca.marca, v.createdAt, v.updatedAt) FROM Veiculo v WHERE v.empresa.id = :empresaId AND (LOWER(v.placa) LIKE LOWER(CONCAT(:query, '%')) OR LOWER(v.modelo) LIKE LOWER(CONCAT(:query, '%')))")
    org.springframework.data.domain.Page<VeiculoPesquisaDTO> searchByQueryAndEmpresaId(@org.springframework.data.repository.query.Param("query") String query, @org.springframework.data.repository.query.Param("empresaId") Long empresaId, org.springframework.data.domain.Pageable pageable);
    
    @org.springframework.data.jpa.repository.Query("SELECT new tecstock_spring.dto.VeiculoPesquisaDTO(v.id, v.nome, v.placa, v.modelo, v.ano, v.categoria, v.cor, v.quilometragem, v.marca.id, v.marca.marca, v.createdAt, v.updatedAt) FROM Veiculo v WHERE v.empresa.id = :empresaId")
    org.springframework.data.domain.Page<VeiculoPesquisaDTO> findByEmpresaId(Long empresaId, org.springframework.data.domain.Pageable pageable);
}
