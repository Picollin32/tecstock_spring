package tecstock_spring.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tecstock_spring.dto.FuncionarioPesquisaDTO;
import tecstock_spring.model.Funcionario;

public interface FuncionarioService {
    Funcionario salvar(Funcionario funcionario);

    Funcionario buscarPorId(Long id);

    List<Funcionario> listarTodos();

    List<Funcionario> listarMecanicos();

    Funcionario atualizar(Long id, Funcionario funcionario);

    void deletar(Long id);
    
    Page<FuncionarioPesquisaDTO> buscarPaginado(String query, Pageable pageable);
    
    List<FuncionarioPesquisaDTO> listarUltimosParaInicio(int limit);
}
