package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tecstock_spring.model.CategoriaFinanceira;

import java.util.List;
import java.util.Optional;

public interface CategoriaFinanceiraRepository extends JpaRepository<CategoriaFinanceira, Long> {

    List<CategoriaFinanceira> findByEmpresaIdAndAtivoTrueOrderByNomeAsc(Long empresaId);

    Optional<CategoriaFinanceira> findByIdAndEmpresaId(Long id, Long empresaId);

    boolean existsByEmpresaIdAndNomeIgnoreCase(Long empresaId, String nome);

    boolean existsByEmpresaIdAndNomeIgnoreCaseAndIdNot(Long empresaId, String nome, Long id);

    Page<CategoriaFinanceira> findByEmpresaIdAndAtivoTrueAndNomeStartingWithIgnoreCase(
            Long empresaId,
            String query,
            Pageable pageable);
}
