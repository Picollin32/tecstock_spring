package tecstock_spring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tecstock_spring.dto.FuncionarioPesquisaDTO;
import tecstock_spring.model.Funcionario;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    
    @Query("SELECT f FROM Funcionario f WHERE f.cpf = :cpf")
    Funcionario findByCpf(@Param("cpf") String cpf);
    
    @Query("SELECT COUNT(f) > 0 FROM Funcionario f WHERE f.cpf = :cpf AND f.id != :id")
    boolean existsByCpfAndIdNot(@Param("cpf") String cpf, @Param("id") Long id);
    
    @Query("SELECT COUNT(f) > 0 FROM Funcionario f WHERE f.cpf = :cpf")
    boolean existsByCpf(@Param("cpf") String cpf);
    List<Funcionario> findByEmpresaId(Long empresaId);
    Optional<Funcionario> findByIdAndEmpresaId(Long id, Long empresaId);
    
    @Query("SELECT new tecstock_spring.dto.FuncionarioPesquisaDTO(f.id, f.nome, f.cpf, f.telefone, f.email, f.dataNascimento, f.rua, f.numeroCasa, f.bairro, f.cidade, f.uf, f.nivelAcesso, f.createdAt, f.updatedAt) FROM Funcionario f WHERE f.empresa.id = :empresaId AND (LOWER(f.nome) LIKE LOWER(CONCAT(:query, '%')) OR f.cpf LIKE CONCAT(:query, '%'))")
    Page<FuncionarioPesquisaDTO> searchByQueryAndEmpresaId(@Param("query") String query, @Param("empresaId") Long empresaId, Pageable pageable);
    
    @Query("SELECT new tecstock_spring.dto.FuncionarioPesquisaDTO(f.id, f.nome, f.cpf, f.telefone, f.email, f.dataNascimento, f.rua, f.numeroCasa, f.bairro, f.cidade, f.uf, f.nivelAcesso, f.createdAt, f.updatedAt) FROM Funcionario f WHERE f.empresa.id = :empresaId")
    Page<FuncionarioPesquisaDTO> findByEmpresaId(Long empresaId, Pageable pageable);
}
