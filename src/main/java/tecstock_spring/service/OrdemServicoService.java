package tecstock_spring.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tecstock_spring.model.OrdemServico;

import java.time.LocalDateTime;
import java.util.List;

public interface OrdemServicoService {
    
    OrdemServico salvar(OrdemServico ordemServico);

    OrdemServico buscarPorId(Long id);
    
    OrdemServico buscarPorNumeroOS(String numeroOS);

    List<OrdemServico> listarTodos();
    
    List<OrdemServico> pesquisarPorNumeroExato(String numero);
    
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
    
    List<OrdemServico> getFiadosEmAberto();
    
    OrdemServico marcarFiadoComoPago(Long id, Boolean pago);
    
    OrdemServico desbloquearParaEdicao(Long id);
    
    OrdemServico reabrirOS(Long id);
    
    Page<OrdemServico> buscarPaginado(String query, String tipo, Pageable pageable);
}
