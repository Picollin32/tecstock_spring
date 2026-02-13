package tecstock_spring.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tecstock_spring.dto.VeiculoPesquisaDTO;
import tecstock_spring.model.Veiculo;

public interface VeiculoService {
    Veiculo salvar(Veiculo veiculo);

    Veiculo buscarPorId(Long id);

    List<Veiculo> listarTodos();

    Veiculo atualizar(Long id, Veiculo veiculo);

    void deletar(Long id);
    
    Page<VeiculoPesquisaDTO> buscarPaginado(String query, Pageable pageable);
    
    List<VeiculoPesquisaDTO> listarUltimosParaInicio(int limit);
}
