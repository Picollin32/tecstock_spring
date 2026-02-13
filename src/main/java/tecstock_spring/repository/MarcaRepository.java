package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tecstock_spring.dto.MarcaPesquisaDTO;
import tecstock_spring.model.Marca;
import java.util.List;
import java.util.Optional;

public interface MarcaRepository extends JpaRepository<Marca, Long> {
    
    @Query("SELECT m FROM Marca m WHERE LOWER(m.marca) = LOWER(:marca)")
    Marca findByMarcaIgnoreCase(@Param("marca") String marca);
    
    @Query("SELECT COUNT(m) > 0 FROM Marca m WHERE LOWER(m.marca) = LOWER(:marca) AND m.id != :id")
    boolean existsByNomeIgnoreCaseAndIdNot(@Param("marca") String marca, @Param("id") Long id);
    
    @Query("SELECT COUNT(m) > 0 FROM Marca m WHERE LOWER(m.marca) = LOWER(:marca)")
    boolean existsByNomeIgnoreCase(@Param("marca") String marca);
    List<Marca> findByEmpresaId(Long empresaId);
    Optional<Marca> findByIdAndEmpresaId(Long id, Long empresaId);
    
    @Query("SELECT m FROM Marca m WHERE LOWER(m.marca) = LOWER(:marca) AND m.empresa.id = :empresaId")
    Marca findByMarcaIgnoreCaseAndEmpresaId(@Param("marca") String marca, @Param("empresaId") Long empresaId);
    
    @Query("SELECT COUNT(m) > 0 FROM Marca m WHERE LOWER(m.marca) = LOWER(:marca) AND m.id != :id AND m.empresa.id = :empresaId")
    boolean existsByNomeIgnoreCaseAndIdNotAndEmpresaId(@Param("marca") String marca, @Param("id") Long id, @Param("empresaId") Long empresaId);
    
    @Query("SELECT COUNT(m) > 0 FROM Marca m WHERE LOWER(m.marca) = LOWER(:marca) AND m.empresa.id = :empresaId")
    boolean existsByNomeIgnoreCaseAndEmpresaId(@Param("marca") String marca, @Param("empresaId") Long empresaId);
    
    @Query("SELECT new tecstock_spring.dto.MarcaPesquisaDTO(m.id, m.marca, m.createdAt, m.updatedAt) FROM Marca m WHERE m.empresa.id = :empresaId AND LOWER(m.marca) LIKE LOWER(CONCAT(:query, '%'))")
    org.springframework.data.domain.Page<MarcaPesquisaDTO> searchByQueryAndEmpresaId(@Param("query") String query, @Param("empresaId") Long empresaId, org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT new tecstock_spring.dto.MarcaPesquisaDTO(m.id, m.marca, m.createdAt, m.updatedAt) FROM Marca m WHERE m.empresa.id = :empresaId")
    org.springframework.data.domain.Page<MarcaPesquisaDTO> findByEmpresaId(Long empresaId, org.springframework.data.domain.Pageable pageable);
}
