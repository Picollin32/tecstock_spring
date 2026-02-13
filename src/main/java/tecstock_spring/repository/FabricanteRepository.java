package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tecstock_spring.dto.FabricantePesquisaDTO;
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
    
    @Query("SELECT new tecstock_spring.dto.FabricantePesquisaDTO(f.id, f.nome, f.createdAt, f.updatedAt) FROM Fabricante f WHERE f.empresa.id = :empresaId AND LOWER(f.nome) LIKE LOWER(CONCAT(:query, '%'))")
    org.springframework.data.domain.Page<FabricantePesquisaDTO> searchByQueryAndEmpresaId(@Param("query") String query, @Param("empresaId") Long empresaId, org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT new tecstock_spring.dto.FabricantePesquisaDTO(f.id, f.nome, f.createdAt, f.updatedAt) FROM Fabricante f WHERE f.empresa.id = :empresaId")
    org.springframework.data.domain.Page<FabricantePesquisaDTO> findByEmpresaId(Long empresaId, org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT new tecstock_spring.dto.FabricantePesquisaDTO(f.id, f.nome, f.createdAt, f.updatedAt) FROM Fabricante f WHERE f.empresa.id = :empresaId ORDER BY f.createdAt DESC")
    List<FabricantePesquisaDTO> findTopByEmpresaIdOrderByCreatedAtDesc(@Param("empresaId") Long empresaId, org.springframework.data.domain.Pageable pageable);
}