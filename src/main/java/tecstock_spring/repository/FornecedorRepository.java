package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tecstock_spring.dto.FornecedorPesquisaDTO;
import tecstock_spring.model.Fornecedor;
import java.util.List;
import java.util.Optional;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    Optional<Fornecedor> findByCnpj(String cnpj);
    boolean existsByCnpj(String cnpj);
    boolean existsByCnpjAndIdNot(String cnpj, Long id);

    List<Fornecedor> findByEmpresaId(Long empresaId);
    Optional<Fornecedor> findByIdAndEmpresaId(Long id, Long empresaId);
    Optional<Fornecedor> findByCnpjAndEmpresaId(String cnpj, Long empresaId);
    boolean existsByCnpjAndEmpresaId(String cnpj, Long empresaId);
    boolean existsByCnpjAndIdNotAndEmpresaId(String cnpj, Long id, Long empresaId);
    
    @org.springframework.data.jpa.repository.Query("SELECT new tecstock_spring.dto.FornecedorPesquisaDTO(f.id, f.nome, f.cnpj, f.telefone, f.email, f.margemLucro, f.rua, f.numeroCasa, f.bairro, f.cidade, f.uf, f.createdAt, f.updatedAt) FROM Fornecedor f WHERE f.empresa.id = :empresaId AND (LOWER(f.nome) LIKE LOWER(CONCAT(:query, '%')) OR f.cnpj LIKE CONCAT(:query, '%'))")
    org.springframework.data.domain.Page<FornecedorPesquisaDTO> searchByQueryAndEmpresaId(@org.springframework.data.repository.query.Param("query") String query, @org.springframework.data.repository.query.Param("empresaId") Long empresaId, org.springframework.data.domain.Pageable pageable);
    
    @org.springframework.data.jpa.repository.Query("SELECT new tecstock_spring.dto.FornecedorPesquisaDTO(f.id, f.nome, f.cnpj, f.telefone, f.email, f.margemLucro, f.rua, f.numeroCasa, f.bairro, f.cidade, f.uf, f.createdAt, f.updatedAt) FROM Fornecedor f WHERE f.empresa.id = :empresaId")
    org.springframework.data.domain.Page<FornecedorPesquisaDTO> findByEmpresaId(Long empresaId, org.springframework.data.domain.Pageable pageable);
    
    @org.springframework.data.jpa.repository.Query("SELECT new tecstock_spring.dto.FornecedorPesquisaDTO(f.id, f.nome, f.cnpj, f.telefone, f.email, f.margemLucro, f.rua, f.numeroCasa, f.bairro, f.cidade, f.uf, f.createdAt, f.updatedAt) FROM Fornecedor f WHERE f.empresa.id = :empresaId ORDER BY f.createdAt DESC")
    List<FornecedorPesquisaDTO> findTopByEmpresaIdOrderByCreatedAtDesc(@org.springframework.data.repository.query.Param("empresaId") Long empresaId, org.springframework.data.domain.Pageable pageable);
}
