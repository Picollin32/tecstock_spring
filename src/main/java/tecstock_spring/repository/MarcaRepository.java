package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
}
