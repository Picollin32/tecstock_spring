package tecstock_spring.service;

import java.util.List;
import tecstock_spring.model.Fabricante;

public interface FabricanteService {
    Fabricante salvar(Fabricante fabricante);

    Fabricante buscarPorId(Long id);

    List<Fabricante> listarTodos();

    Fabricante atualizar(Long id, Fabricante fabricante);

    void deletar(Long id);
}
