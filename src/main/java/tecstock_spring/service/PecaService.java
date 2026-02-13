package tecstock_spring.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tecstock_spring.dto.AjusteEstoqueDTO;
import tecstock_spring.dto.PecaPesquisaDTO;
import tecstock_spring.model.Peca;

public interface PecaService {
    Peca salvar(Peca peca);

    Peca buscarPorId(Long id);

    Peca buscarPorCodigo(String codigo);

    List<Peca> listarTodas();
    
    List<Peca> listarEmUso();
    
    void atualizarUnidadesUsadas();

    Peca atualizar(Long id, Peca peca);
    
    Peca ajustarEstoque(AjusteEstoqueDTO ajusteDTO);

    void deletar(Long id);
    
    Page<PecaPesquisaDTO> buscarPaginado(String query, Pageable pageable);
    
    List<PecaPesquisaDTO> listarUltimosParaInicio(int limit);
}