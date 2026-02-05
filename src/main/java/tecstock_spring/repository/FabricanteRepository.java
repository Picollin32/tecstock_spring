package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tecstock_spring.model.Fabricante;
import java.util.List;
import java.util.Optional;

@Repository
public interface FabricanteRepository extends JpaRepository<Fabricante, Long> {
    
    @Query("SELECT f FROM Fabricante f WHERE LOWER(f.nome) = LOWER(:nome)")
    Fabricante findByNomeIgnoreCase(@Param("nome") String nome);
    
    @Query("SELECT COUNT(f) > 0 FROM Fabricante f WHERE LOWER(f.nome) = LOWER(:nome) AND f.id != :id")
    boolean existsByNomeIgnoreCaseAndIdNot(@Param("nome") String nome, @Param("id") Long id);
    
    @Query("SELECT COUNT(f) > 0 FROM Fabricante f WHERE LOWER(f.nome) = LOWER(:nome)")
    boolean existsByNomeIgnoreCase(@Param("nome") String nome);

    List<Fabricante> findByEmpresaId(Long empresaId);
    Optional<Fabricante> findByIdAndEmpresaId(Long id, Long empresaId);
    
    @Query("SELECT f FROM Fabricante f WHERE LOWER(f.nome) = LOWER(:nome) AND f.empresa.id = :empresaId")
    Fabricante findByNomeIgnoreCaseAndEmpresaId(@Param("nome") String nome, @Param("empresaId") Long empresaId);
    
    @Query("SELECT COUNT(f) > 0 FROM Fabricante f WHERE LOWER(f.nome) = LOWER(:nome) AND f.id != :id AND f.empresa.id = :empresaId")
    boolean existsByNomeIgnoreCaseAndIdNotAndEmpresaId(@Param("nome") String nome, @Param("id") Long id, @Param("empresaId") Long empresaId);
    
    @Query("SELECT COUNT(f) > 0 FROM Fabricante f WHERE LOWER(f.nome) = LOWER(:nome) AND f.empresa.id = :empresaId")
    boolean existsByNomeIgnoreCaseAndEmpresaId(@Param("nome") String nome, @Param("empresaId") Long empresaId);
}