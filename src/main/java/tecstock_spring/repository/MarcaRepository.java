package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tecstock_spring.model.Marca;

public interface MarcaRepository extends JpaRepository<Marca, Long> {
    
    @Query("SELECT m FROM Marca m WHERE LOWER(m.marca) = LOWER(:marca)")
    Marca findByMarcaIgnoreCase(@Param("marca") String marca);
    
    @Query("SELECT COUNT(m) > 0 FROM Marca m WHERE LOWER(m.marca) = LOWER(:marca) AND m.id != :id")
    boolean existsByNomeIgnoreCaseAndIdNot(@Param("marca") String marca, @Param("id") Long id);
    
    @Query("SELECT COUNT(m) > 0 FROM Marca m WHERE LOWER(m.marca) = LOWER(:marca)")
    boolean existsByNomeIgnoreCase(@Param("marca") String marca);
}
