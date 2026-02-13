package tecstock_spring.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tecstock_spring.model.Checklist;

public interface ChecklistService {
    Checklist salvar(Checklist checklist);

    Checklist buscarPorId(Long id);

    List<Checklist> listarTodos();

    Checklist atualizar(Long id, Checklist checklist);
    
    List<Checklist> pesquisarPorNumeroExato(Integer numero);

    void deletar(Long id);
    
    boolean fecharChecklist(Long id);
    
    boolean reabrirChecklist(Long id);
    
    Page<Checklist> buscarPaginado(String query, String tipo, Pageable pageable);
}
