package tecstock_spring.service;

import tecstock_spring.model.OrdemServico;
import tecstock_spring.model.ServicoOrdemServico;

import java.util.List;

public interface ServicoOrdemServicoService {
    
    ServicoOrdemServico salvar(ServicoOrdemServico servicoOrdemServico);
    
    void registrarServicosRealizados(OrdemServico ordemServico);
    
    List<ServicoOrdemServico> buscarPorNumeroOS(String numeroOS);
    
    List<ServicoOrdemServico> listarTodos();
    
    List<ServicoOrdemServico> buscarPorServicoId(Long servicoId);
    
    void deletar(Long id);
}
