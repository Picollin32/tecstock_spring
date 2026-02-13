package tecstock_spring.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tecstock_spring.model.Servico;

public interface ServicoService {
    Servico salvar(Servico servico);

    Servico buscarPorId(Long id);

    List<Servico> listarTodos();
    
    List<Servico> listarComPendentes();
    
    void atualizarUnidadesUsadas();

    Servico atualizar(Long id, Servico servico);

    void deletar(Long id);
    
    Page<tecstock_spring.dto.ServicoPesquisaDTO> buscarPaginado(String query, Pageable pageable);
}