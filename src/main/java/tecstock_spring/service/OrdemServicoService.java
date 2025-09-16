package tecstock_spring.service;

import tecstock_spring.model.OrdemServico;

import java.time.LocalDateTime;
import java.util.List;

public interface OrdemServicoService {
    
    OrdemServico salvar(OrdemServico ordemServico);

    OrdemServico buscarPorId(Long id);
    
    OrdemServico buscarPorNumeroOS(String numeroOS);

    List<OrdemServico> listarTodos();
    
    List<OrdemServico> listarPorCliente(String clienteCpf);
    
    List<OrdemServico> listarPorVeiculo(String veiculoPlaca);
    
    List<OrdemServico> listarPorStatus(String status);
    
    List<OrdemServico> listarPorChecklist(Long checklistId);
    
    List<OrdemServico> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim);

    OrdemServico atualizar(Long id, OrdemServico ordemServico);
    
    OrdemServico atualizarApenasStatus(Long id, String novoStatus);

    OrdemServico fecharOrdemServico(Long id);

    void deletar(Long id);
    
    void processarEstoquePecas(OrdemServico ordemServico, boolean isNovaOS);
}
