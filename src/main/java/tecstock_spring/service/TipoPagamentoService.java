package tecstock_spring.service;

import java.util.List;
import tecstock_spring.model.TipoPagamento;

public interface TipoPagamentoService {
    TipoPagamento salvar(TipoPagamento tipoPagamento);

    TipoPagamento buscarPorId(Long id);

    List<TipoPagamento> listarTodos();

    TipoPagamento atualizar(Long id, TipoPagamento tipoPagamento);

    void deletar(Long id);
}
