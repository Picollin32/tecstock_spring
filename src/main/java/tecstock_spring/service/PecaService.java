package tecstock_spring.service;

import java.util.List;
import tecstock_spring.model.Peca;

public interface PecaService {
    Peca salvar(Peca peca);

    Peca buscarPorId(Long id);

    Peca buscarPorCodigo(String codigo);

    List<Peca> listarTodas();

    Peca atualizar(Long id, Peca peca);

    void deletar(Long id);
}