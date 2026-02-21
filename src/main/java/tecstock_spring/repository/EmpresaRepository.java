package tecstock_spring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tecstock_spring.model.Empresa;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    
    Optional<Empresa> findByCnpj(String cnpj);
    
    boolean existsByCnpj(String cnpj);
    
    boolean existsByCnpjAndIdNot(String cnpj, Long id);

    @Query("SELECT COUNT(f) > 0 FROM Funcionario f WHERE f.empresa.id = :empresaId")
    boolean hasFuncionarios(@Param("empresaId") Long empresaId);
    
    @Query("SELECT COUNT(c) > 0 FROM Cliente c WHERE c.empresa.id = :empresaId")
    boolean hasClientes(@Param("empresaId") Long empresaId);
    
    @Query("SELECT COUNT(v) > 0 FROM Veiculo v WHERE v.empresa.id = :empresaId")
    boolean hasVeiculos(@Param("empresaId") Long empresaId);
    
    @Query("SELECT COUNT(p) > 0 FROM Peca p WHERE p.empresa.id = :empresaId")
    boolean hasPecas(@Param("empresaId") Long empresaId);
    
    @Query("SELECT COUNT(s) > 0 FROM Servico s WHERE s.empresa.id = :empresaId")
    boolean hasServicos(@Param("empresaId") Long empresaId);
    
    @Query("SELECT COUNT(o) > 0 FROM OrdemServico o WHERE o.empresa.id = :empresaId")
    boolean hasOrdensServico(@Param("empresaId") Long empresaId);
    
    @Query("SELECT COUNT(o) > 0 FROM Orcamento o WHERE o.empresa.id = :empresaId")
    boolean hasOrcamentos(@Param("empresaId") Long empresaId);
    
    @Query("SELECT COUNT(a) > 0 FROM Agendamento a WHERE a.empresa.id = :empresaId")
    boolean hasAgendamentos(@Param("empresaId") Long empresaId);
    
    @Query("SELECT COUNT(c) > 0 FROM Checklist c WHERE c.empresa.id = :empresaId")
    boolean hasChecklists(@Param("empresaId") Long empresaId);
    
    @Query("SELECT COUNT(m) > 0 FROM MovimentacaoEstoque m WHERE m.empresa.id = :empresaId")
    boolean hasMovimentacoesEstoque(@Param("empresaId") Long empresaId);
    
    @Query("SELECT COUNT(f) > 0 FROM Fornecedor f WHERE f.empresa.id = :empresaId")
    boolean hasFornecedores(@Param("empresaId") Long empresaId);
    
    @Query("SELECT COUNT(f) > 0 FROM Fabricante f WHERE f.empresa.id = :empresaId")
    boolean hasFabricantes(@Param("empresaId") Long empresaId);
    
    @Query("SELECT COUNT(m) > 0 FROM Marca m WHERE m.empresa.id = :empresaId")
    boolean hasMarcas(@Param("empresaId") Long empresaId);

    @Query("SELECT e FROM Empresa e WHERE LOWER(e.razaoSocial) LIKE LOWER(CONCAT(:query, '%')) OR LOWER(e.nomeFantasia) LIKE LOWER(CONCAT(:query, '%')) OR e.cnpj LIKE CONCAT(:query, '%') ORDER BY e.id DESC")
    Page<Empresa> searchByQuery(@Param("query") String query, Pageable pageable);

    @Query("SELECT e FROM Empresa e ORDER BY e.id DESC")
    List<Empresa> findTopRecentEmpresas(Pageable pageable);
}
