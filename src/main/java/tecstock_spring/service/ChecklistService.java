package tecstock_spring.service;

import java.util.List;
import tecstock_spring.model.Checklist;

public interface ChecklistService {
    Checklist salvar(Checklist checklist);

    Checklist buscarPorId(Long id);

    List<Checklist> listarTodos();

    Checklist atualizar(Long id, Checklist checklist);

    void deletar(Long id);
}
