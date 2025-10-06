package tecstock_spring.service;

import java.util.List;
import tecstock_spring.dto.AjusteEstoqueDTO;
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
}