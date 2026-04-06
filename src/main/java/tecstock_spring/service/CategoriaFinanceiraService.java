package tecstock_spring.service;

import tecstock_spring.model.CategoriaFinanceira;

import java.util.List;

public interface CategoriaFinanceiraService {

    List<CategoriaFinanceira> listarAtivas();

    CategoriaFinanceira criar(CategoriaFinanceira categoria);

    CategoriaFinanceira atualizar(Long id, CategoriaFinanceira categoria);

    void deletar(Long id);
}
