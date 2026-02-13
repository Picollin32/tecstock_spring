package tecstock_spring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tecstock_spring.dto.ClientePesquisaDTO;
import tecstock_spring.model.Cliente;
import tecstock_spring.model.Empresa;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
    boolean existsByCpfAndIdNot(String cpf, Long id);

    List<Cliente> findByEmpresa(Empresa empresa);
    List<Cliente> findByEmpresaId(Long empresaId);
    Optional<Cliente> findByIdAndEmpresaId(Long id, Long empresaId);
    boolean existsByCpfAndEmpresaId(String cpf, Long empresaId);
    boolean existsByCpfAndIdNotAndEmpresaId(String cpf, Long id, Long empresaId);
    
    @Query("SELECT new tecstock_spring.dto.ClientePesquisaDTO(c.id, c.nome, c.cpf, c.telefone, c.email, c.dataNascimento, c.rua, c.numeroCasa, c.bairro, c.cidade, c.uf, c.createdAt, c.updatedAt) FROM Cliente c WHERE c.empresa.id = :empresaId AND (LOWER(c.nome) LIKE LOWER(CONCAT(:query, '%')) OR c.cpf LIKE CONCAT(:query, '%'))")
    Page<ClientePesquisaDTO> searchByQueryAndEmpresaId(@Param("query") String query, @Param("empresaId") Long empresaId, Pageable pageable);
    
    @Query("SELECT new tecstock_spring.dto.ClientePesquisaDTO(c.id, c.nome, c.cpf, c.telefone, c.email, c.dataNascimento, c.rua, c.numeroCasa, c.bairro, c.cidade, c.uf, c.createdAt, c.updatedAt) FROM Cliente c WHERE c.empresa.id = :empresaId")
    Page<ClientePesquisaDTO> findByEmpresaId(Long empresaId, Pageable pageable);
}
