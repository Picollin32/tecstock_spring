package tecstock_spring.service;

import java.util.List;
import tecstock_spring.model.Funcionario;

public interface FuncionarioService {
    Funcionario salvar(Funcionario funcionario);

    Funcionario buscarPorId(Long id);

    List<Funcionario> listarTodos();

    List<Funcionario> listarMecanicos();

    Funcionario atualizar(Long id, Funcionario funcionario);

    void deletar(Long id);
}
