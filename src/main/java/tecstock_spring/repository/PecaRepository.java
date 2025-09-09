package tecstock_spring.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tecstock_spring.model.Peca;

public interface PecaRepository extends JpaRepository<Peca, Long> {
    
    @Query("SELECT p FROM Peca p WHERE p.codigoFabricante = :codigo AND p.fornecedor.id = :fornecedorId")
    Optional<Peca> findByCodigoFabricanteAndFornecedorId(@Param("codigo") String codigoFabricante, @Param("fornecedorId") Long fornecedorId);
    
    @Query("SELECT p FROM Peca p WHERE p.codigoFabricante = :codigo AND p.fornecedor IS NULL")
    Optional<Peca> findByCodigoFabricanteAndFornecedorIsNull(@Param("codigo") String codigoFabricante);
    
    @Query("SELECT p FROM Peca p WHERE p.codigoFabricante = :codigo")
    List<Peca> findByCodigoFabricante(@Param("codigo") String codigoFabricante);
}