package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tecstock_spring.model.Fabricante;

@Repository
public interface FabricanteRepository extends JpaRepository<Fabricante, Long> {
    
    @Query("SELECT f FROM Fabricante f WHERE LOWER(f.nome) = LOWER(:nome)")
    Fabricante findByNomeIgnoreCase(@Param("nome") String nome);
    
    @Query("SELECT COUNT(f) > 0 FROM Fabricante f WHERE LOWER(f.nome) = LOWER(:nome) AND f.id != :id")
    boolean existsByNomeIgnoreCaseAndIdNot(@Param("nome") String nome, @Param("id") Long id);
    
    @Query("SELECT COUNT(f) > 0 FROM Fabricante f WHERE LOWER(f.nome) = LOWER(:nome)")
    boolean existsByNomeIgnoreCase(@Param("nome") String nome);
}