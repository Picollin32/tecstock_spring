package tecstock_spring.service;

import tecstock_spring.model.Orcamento;

import java.time.LocalDateTime;
import java.util.List;

public interface OrcamentoService {
    
    Orcamento salvar(Orcamento orcamento);

    Orcamento buscarPorId(Long id);
    
    Orcamento buscarPorNumeroOrcamento(String numeroOrcamento);

    List<Orcamento> listarTodos();
    
    List<Orcamento> listarPorCliente(String clienteCpf);
    
    List<Orcamento> listarPorVeiculo(String veiculoPlaca);
    
    
    List<Orcamento> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim);

    Orcamento atualizar(Long id, Orcamento orcamento);

    void deletar(Long id);
}