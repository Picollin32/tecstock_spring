package tecstock_spring.service;

import java.util.List;
import tecstock_spring.model.Marca;

public interface MarcaService {
    Marca salvar(Marca marca);

    Marca buscarPorId(Long id);

    List<Marca> listarTodos();

    Marca atualizar(Long id, Marca marca);

    void deletar(Long id);
}
