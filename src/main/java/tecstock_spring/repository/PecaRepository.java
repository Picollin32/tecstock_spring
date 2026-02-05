package tecstock_spring.repository;

import java.util.List;
import java.util.Optional;
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
    
    boolean existsByFabricante(Fabricante fabricante);
    boolean existsByFornecedor(Fornecedor fornecedor);
    
    @Modifying
    @Transactional
    @Query("UPDATE Peca p SET p.quantidadeEstoque = :novaQuantidade WHERE p.id = :pecaId")
    void atualizarEstoqueSemTriggerUpdate(@Param("pecaId") Long pecaId, @Param("novaQuantidade") int novaQuantidade);
    
    @Modifying
    @Transactional
    @Query("UPDATE Peca p SET p.unidadesUsadasEmOS = :unidadesUsadas WHERE p.id = :pecaId")
    void atualizarUnidadesUsadasSemTriggerUpdate(@Param("pecaId") Long pecaId, @Param("unidadesUsadas") int unidadesUsadas);
    

    @Modifying
    @Transactional
    @Query("UPDATE Peca p SET p.quantidadeEstoque = p.quantidadeEstoque + :quantidade WHERE p.id = :pecaId")
    int incrementarEstoqueAtomico(@Param("pecaId") Long pecaId, @Param("quantidade") int quantidade);

    @Modifying
    @Transactional
    @Query("UPDATE Peca p SET p.quantidadeEstoque = p.quantidadeEstoque - :quantidade WHERE p.id = :pecaId AND p.quantidadeEstoque >= :quantidade")
    int decrementarEstoqueAtomico(@Param("pecaId") Long pecaId, @Param("quantidade") int quantidade);
    List<Peca> findByEmpresaId(Long empresaId);
    Optional<Peca> findByIdAndEmpresaId(Long id, Long empresaId);
    
    @Query("SELECT p FROM Peca p WHERE p.codigoFabricante = :codigo AND p.fornecedor.id = :fornecedorId AND p.empresa.id = :empresaId")
    Optional<Peca> findByCodigoFabricanteAndFornecedorIdAndEmpresaId(@Param("codigo") String codigoFabricante, @Param("fornecedorId") Long fornecedorId, @Param("empresaId") Long empresaId);
}