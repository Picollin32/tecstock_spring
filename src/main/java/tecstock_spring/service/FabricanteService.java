package tecstock_spring.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tecstock_spring.dto.FabricantePesquisaDTO;
import tecstock_spring.model.Fabricante;

public interface FabricanteService {
    Fabricante salvar(Fabricante fabricante);

    Fabricante buscarPorId(Long id);

    List<Fabricante> listarTodos();

    Fabricante atualizar(Long id, Fabricante fabricante);

    void deletar(Long id);
    
    Page<FabricantePesquisaDTO> buscarPaginado(String query, Pageable pageable);
}
