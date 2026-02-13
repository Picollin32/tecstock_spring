package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tecstock_spring.dto.ServicoPesquisaDTO;
import tecstock_spring.model.Servico;
import java.util.List;
import java.util.Optional;

public interface ServicoRepository extends JpaRepository<Servico, Long> {
    
    @Query("SELECT s FROM Servico s WHERE LOWER(s.nome) = LOWER(:nome)")
    Servico findByNomeIgnoreCase(@Param("nome") String nome);
    
    @Query("SELECT COUNT(s) > 0 FROM Servico s WHERE LOWER(s.nome) = LOWER(:nome) AND s.id != :id")
    boolean existsByNomeIgnoreCaseAndIdNot(@Param("nome") String nome, @Param("id") Long id);
    
    @Query("SELECT COUNT(s) > 0 FROM Servico s WHERE LOWER(s.nome) = LOWER(:nome)")
    boolean existsByNomeIgnoreCase(@Param("nome") String nome);
    
    List<Servico> findByEmpresaId(Long empresaId);
    Optional<Servico> findByIdAndEmpresaId(Long id, Long empresaId);
    
    @Query("SELECT s FROM Servico s WHERE LOWER(s.nome) = LOWER(:nome) AND s.empresa.id = :empresaId")
    Servico findByNomeIgnoreCaseAndEmpresaId(@Param("nome") String nome, @Param("empresaId") Long empresaId);
    
    @Query("SELECT COUNT(s) > 0 FROM Servico s WHERE LOWER(s.nome) = LOWER(:nome) AND s.id != :id AND s.empresa.id = :empresaId")
    boolean existsByNomeIgnoreCaseAndIdNotAndEmpresaId(@Param("nome") String nome, @Param("id") Long id, @Param("empresaId") Long empresaId);
    
    @Query("SELECT COUNT(s) > 0 FROM Servico s WHERE LOWER(s.nome) = LOWER(:nome) AND s.empresa.id = :empresaId")
    boolean existsByNomeIgnoreCaseAndEmpresaId(@Param("nome") String nome, @Param("empresaId") Long empresaId);
    
    @Query("SELECT new tecstock_spring.dto.ServicoPesquisaDTO(s.id, s.nome, s.precoPasseio, s.precoCaminhonete, s.createdAt, s.updatedAt) FROM Servico s WHERE s.empresa.id = :empresaId AND LOWER(s.nome) LIKE LOWER(CONCAT(:query, '%'))")
    org.springframework.data.domain.Page<ServicoPesquisaDTO> searchByQueryAndEmpresaId(@Param("query") String query, @Param("empresaId") Long empresaId, org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT new tecstock_spring.dto.ServicoPesquisaDTO(s.id, s.nome, s.precoPasseio, s.precoCaminhonete, s.createdAt, s.updatedAt) FROM Servico s WHERE s.empresa.id = :empresaId")
    org.springframework.data.domain.Page<ServicoPesquisaDTO> findByEmpresaId(Long empresaId, org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT new tecstock_spring.dto.ServicoPesquisaDTO(s.id, s.nome, s.precoPasseio, s.precoCaminhonete, s.createdAt, s.updatedAt) FROM Servico s WHERE s.empresa.id = :empresaId ORDER BY s.id DESC")
    List<ServicoPesquisaDTO> findTopByEmpresaIdOrderByIdDesc(@Param("empresaId") Long empresaId, org.springframework.data.domain.Pageable pageable);
}