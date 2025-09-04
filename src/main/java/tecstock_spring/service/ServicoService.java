package tecstock_spring.service;

import java.util.List;
import tecstock_spring.model.Servico;

public interface ServicoService {
    Servico salvar(Servico servico);

    Servico buscarPorId(Long id);

    List<Servico> listarTodos();

    Servico atualizar(Long id, Servico servico);

    void deletar(Long id);
}