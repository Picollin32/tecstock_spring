package tecstock_spring.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tecstock_spring.dto.FornecedorPesquisaDTO;
import tecstock_spring.model.Fornecedor;

public interface FornecedorService {
    Fornecedor salvar(Fornecedor fornecedor);

    Fornecedor buscarPorId(Long id);

    List<Fornecedor> listarTodos();

    Fornecedor atualizar(Long id, Fornecedor fornecedor);

    void deletar(Long id);
    
    Page<FornecedorPesquisaDTO> buscarPaginado(String query, Pageable pageable);
}
