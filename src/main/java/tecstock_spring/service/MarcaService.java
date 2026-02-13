package tecstock_spring.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tecstock_spring.dto.MarcaPesquisaDTO;
import tecstock_spring.model.Marca;

public interface MarcaService {
    Marca salvar(Marca marca);

    Marca buscarPorId(Long id);

    List<Marca> listarTodos();

    Marca atualizar(Long id, Marca marca);

    void deletar(Long id);
    
    Page<MarcaPesquisaDTO> buscarPaginado(String query, Pageable pageable);
    
    List<MarcaPesquisaDTO> listarUltimosParaInicio(int limit);
}
