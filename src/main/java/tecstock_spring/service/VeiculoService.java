package tecstock_spring.service;

import java.util.List;
import tecstock_spring.model.Veiculo;

public interface VeiculoService {
    Veiculo salvar(Veiculo veiculo);

    Veiculo buscarPorId(Long id);

    List<Veiculo> listarTodos();

    Veiculo atualizar(Long id, Veiculo veiculo);

    void deletar(Long id);
}
