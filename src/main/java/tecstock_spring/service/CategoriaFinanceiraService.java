package tecstock_spring.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tecstock_spring.model.CategoriaFinanceira;

import java.util.List;

public interface CategoriaFinanceiraService {

    List<CategoriaFinanceira> listarAtivas();

    Page<CategoriaFinanceira> buscarPaginado(String query, Pageable pageable);

    CategoriaFinanceira criar(CategoriaFinanceira categoria);

    CategoriaFinanceira atualizar(Long id, CategoriaFinanceira categoria);

    void deletar(Long id);
}
