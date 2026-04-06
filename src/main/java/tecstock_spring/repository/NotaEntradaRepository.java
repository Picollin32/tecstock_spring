package tecstock_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tecstock_spring.model.NotaEntrada;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotaEntradaRepository extends JpaRepository<NotaEntrada, Long> {

    boolean existsByNumeroNotaFiscalAndFornecedorIdAndEmpresaId(String numeroNotaFiscal, Long fornecedorId, Long empresaId);

    Optional<NotaEntrada> findByNumeroNotaFiscalAndFornecedorIdAndEmpresaId(String numeroNotaFiscal, Long fornecedorId, Long empresaId);

    List<NotaEntrada> findByEmpresaIdOrderByDataEntradaDesc(Long empresaId);

    void deleteByNumeroNotaFiscalAndFornecedorIdAndEmpresaId(String numeroNotaFiscal, Long fornecedorId, Long empresaId);
}
