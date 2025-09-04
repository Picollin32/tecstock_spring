package tecstock_spring.service;

import java.util.List;
import tecstock_spring.model.Cliente;

public interface ClienteService {
    Cliente salvar(Cliente cliente);

    Cliente buscarPorId(Long id);

    List<Cliente> listarTodos();

    Cliente atualizar(Long id, Cliente cliente);

    void deletar(Long id);
}
