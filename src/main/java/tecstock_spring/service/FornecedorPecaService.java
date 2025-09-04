package tecstock_spring.service;

import java.util.List;
import tecstock_spring.model.FornecedorPeca;
import tecstock_spring.model.FornecedorPecaId;

public interface FornecedorPecaService {
    FornecedorPeca salvar(FornecedorPeca fornecedorPeca);

    FornecedorPeca buscarPorId(FornecedorPecaId id);

    List<FornecedorPeca> listarTodos();

    FornecedorPeca atualizar(FornecedorPecaId id, FornecedorPeca fornecedorPeca);

    void deletar(FornecedorPecaId id);
}