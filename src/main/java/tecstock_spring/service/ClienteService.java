package tecstock_spring.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tecstock_spring.dto.ClientePesquisaDTO;
import tecstock_spring.model.Cliente;

public interface ClienteService {
    Cliente salvar(Cliente cliente);

    Cliente buscarPorId(Long id);

    List<Cliente> listarTodos();

    Cliente atualizar(Long id, Cliente cliente);

    void deletar(Long id);
    
    Page<ClientePesquisaDTO> buscarPaginado(String query, Pageable pageable);
    
    List<ClientePesquisaDTO> listarUltimosParaInicio(int limit);
}
