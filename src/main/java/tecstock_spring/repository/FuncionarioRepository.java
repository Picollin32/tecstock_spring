package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tecstock_spring.model.Funcionario;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    
    @Query("SELECT f FROM Funcionario f WHERE f.cpf = :cpf")
    Funcionario findByCpf(@Param("cpf") String cpf);
    
    @Query("SELECT COUNT(f) > 0 FROM Funcionario f WHERE f.cpf = :cpf AND f.id != :id")
    boolean existsByCpfAndIdNot(@Param("cpf") String cpf, @Param("id") Long id);
    
    @Query("SELECT COUNT(f) > 0 FROM Funcionario f WHERE f.cpf = :cpf")
    boolean existsByCpf(@Param("cpf") String cpf);
}
