package tecstock_spring.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import tecstock_spring.model.Peca;
import tecstock_spring.model.Fabricante;
import tecstock_spring.model.Fornecedor;

public interface PecaRepository extends JpaRepository<Peca, Long> {
    
    @Query("SELECT p FROM Peca p WHERE p.codigoFabricante = :codigo AND p.fornecedor.id = :fornecedorId")
    Optional<Peca> findByCodigoFabricanteAndFornecedorId(@Param("codigo") String codigoFabricante, @Param("fornecedorId") Long fornecedorId);
    
    @Query("SELECT p FROM Peca p WHERE p.codigoFabricante = :codigo AND p.fornecedor IS NULL")
    Optional<Peca> findByCodigoFabricanteAndFornecedorIsNull(@Param("codigo") String codigoFabricante);
    
    @Query("SELECT p FROM Peca p WHERE p.codigoFabricante = :codigo")
    List<Peca> findByCodigoFabricante(@Param("codigo") String codigoFabricante);

    @Query("SELECT p FROM Peca p WHERE p.codigoFabricante = :codigo AND p.empresa.id = :empresaId")
    List<Peca> findByCodigoFabricanteAndEmpresaId(@Param("codigo") String codigoFabricante, @Param("empresaId") Long empresaId);
    
    @Query("SELECT p FROM Peca p WHERE p.codigoFabricante = :codigo AND p.fornecedor IS NULL AND p.empresa.id = :empresaId")
    Optional<Peca> findByCodigoFabricanteAndFornecedorIsNullAndEmpresaId(@Param("codigo") String codigoFabricante, @Param("empresaId") Long empresaId);
    
    boolean existsByFabricante(Fabricante fabricante);
    boolean existsByFornecedor(Fornecedor fornecedor);
    
    @Modifying
    @Transactional
    @Query("UPDATE Peca p SET p.quantidadeEstoque = :novaQuantidade WHERE p.id = :pecaId AND p.empresa.id = :empresaId")
    void atualizarEstoqueSemTriggerUpdate(@Param("pecaId") Long pecaId, @Param("novaQuantidade") int novaQuantidade, @Param("empresaId") Long empresaId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Peca p SET p.unidadesUsadasEmOS = :unidadesUsadas WHERE p.id = :pecaId AND p.empresa.id = :empresaId")
    void atualizarUnidadesUsadasSemTriggerUpdate(@Param("pecaId") Long pecaId, @Param("unidadesUsadas") int unidadesUsadas, @Param("empresaId") Long empresaId);
    

    @Modifying
    @Transactional
    @Query("UPDATE Peca p SET p.quantidadeEstoque = p.quantidadeEstoque + :quantidade WHERE p.id = :pecaId AND p.empresa.id = :empresaId")
    int incrementarEstoqueAtomico(@Param("pecaId") Long pecaId, @Param("quantidade") int quantidade, @Param("empresaId") Long empresaId);

    @Modifying
    @Transactional
    @Query("UPDATE Peca p SET p.quantidadeEstoque = p.quantidadeEstoque - :quantidade WHERE p.id = :pecaId AND p.quantidadeEstoque >= :quantidade AND p.empresa.id = :empresaId")
    int decrementarEstoqueAtomico(@Param("pecaId") Long pecaId, @Param("quantidade") int quantidade, @Param("empresaId") Long empresaId);
    List<Peca> findByEmpresaId(Long empresaId);
    Optional<Peca> findByIdAndEmpresaId(Long id, Long empresaId);
    
    @Query("SELECT p FROM Peca p WHERE p.codigoFabricante = :codigo AND p.fornecedor.id = :fornecedorId AND p.empresa.id = :empresaId")
    Optional<Peca> findByCodigoFabricanteAndFornecedorIdAndEmpresaId(@Param("codigo") String codigoFabricante, @Param("fornecedorId") Long fornecedorId, @Param("empresaId") Long empresaId);
    
    @Query("SELECT new tecstock_spring.dto.PecaPesquisaDTO(p.id, p.nome, p.codigoFabricante, p.precoUnitario, p.precoFinal, p.quantidadeEstoque, p.estoqueSeguranca, COALESCE(p.unidadesUsadasEmOS, 0), p.fabricante.id, p.fabricante.nome, fo.id, fo.nome, fo.cnpj, fo.telefone, fo.email, fo.margemLucro, fo.rua, fo.numeroCasa, fo.bairro, fo.cidade, fo.uf, p.createdAt, p.updatedAt) FROM Peca p LEFT JOIN p.fornecedor fo WHERE p.empresa.id = :empresaId AND (LOWER(p.nome) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.codigoFabricante) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<tecstock_spring.dto.PecaPesquisaDTO> searchByQueryAndEmpresaId(@Param("query") String query, @Param("empresaId") Long empresaId, Pageable pageable);
    
    @Query("SELECT new tecstock_spring.dto.PecaPesquisaDTO(p.id, p.nome, p.codigoFabricante, p.precoUnitario, p.precoFinal, p.quantidadeEstoque, p.estoqueSeguranca, COALESCE(p.unidadesUsadasEmOS, 0), p.fabricante.id, p.fabricante.nome, fo.id, fo.nome, fo.cnpj, fo.telefone, fo.email, fo.margemLucro, fo.rua, fo.numeroCasa, fo.bairro, fo.cidade, fo.uf, p.createdAt, p.updatedAt) FROM Peca p LEFT JOIN p.fornecedor fo WHERE p.empresa.id = :empresaId")
    Page<tecstock_spring.dto.PecaPesquisaDTO> findByEmpresaId(@Param("empresaId") Long empresaId, Pageable pageable);
    
    @Query("SELECT new tecstock_spring.dto.PecaPesquisaDTO(p.id, p.nome, p.codigoFabricante, p.precoUnitario, p.precoFinal, p.quantidadeEstoque, p.estoqueSeguranca, COALESCE(p.unidadesUsadasEmOS, 0), p.fabricante.id, p.fabricante.nome, fo.id, fo.nome, fo.cnpj, fo.telefone, fo.email, fo.margemLucro, fo.rua, fo.numeroCasa, fo.bairro, fo.cidade, fo.uf, p.createdAt, p.updatedAt) FROM Peca p LEFT JOIN p.fornecedor fo WHERE p.empresa.id = :empresaId ORDER BY p.createdAt DESC")
    List<tecstock_spring.dto.PecaPesquisaDTO> findTopByEmpresaIdOrderByCreatedAtDesc(@Param("empresaId") Long empresaId, Pageable pageable);
}