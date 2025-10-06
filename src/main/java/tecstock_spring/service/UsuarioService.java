package tecstock_spring.service;

import java.util.List;
import tecstock_spring.model.Usuario;

public interface UsuarioService {
    Usuario salvar(Usuario usuario);

    Usuario buscarPorId(Long id);

    List<Usuario> listarTodos();

    Usuario atualizar(Long id, Usuario usuario);

    void deletar(Long id);
}
