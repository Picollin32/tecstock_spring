package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tecstock_spring.model.Servico;

public interface ServicoRepository extends JpaRepository<Servico, Long> {
    
    @Query("SELECT s FROM Servico s WHERE LOWER(s.nome) = LOWER(:nome)")
    Servico findByNomeIgnoreCase(@Param("nome") String nome);
    
    @Query("SELECT COUNT(s) > 0 FROM Servico s WHERE LOWER(s.nome) = LOWER(:nome) AND s.id != :id")
    boolean existsByNomeIgnoreCaseAndIdNot(@Param("nome") String nome, @Param("id") Long id);
    
    @Query("SELECT COUNT(s) > 0 FROM Servico s WHERE LOWER(s.nome) = LOWER(:nome)")
    boolean existsByNomeIgnoreCase(@Param("nome") String nome);
}