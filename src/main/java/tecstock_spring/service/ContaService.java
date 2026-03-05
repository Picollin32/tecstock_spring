package tecstock_spring.service;

import tecstock_spring.model.Conta;
import tecstock_spring.model.OrdemServico;

import java.util.List;
import java.util.Map;

public interface ContaService {

    void gerarContasParaOS(OrdemServico ordemServico);

    List<Conta> listarPorMesAno(int mes, int ano);

    List<Conta> listarAPagarPorMesAno(int mes, int ano);

    List<Conta> listarAReceberPorMesAno(int mes, int ano);

    List<Conta> listarAtrasadas();

    Conta adicionarContaPagar(Conta conta);

    Conta marcarComoPago(Long id);

    Conta desmarcarPagamento(Long id);

    void deletar(Long id);

    Conta editar(Long id, Conta dados);

    void removerContasDaOS(Long osId);

    Map<String, Double> resumoMes(int mes, int ano);

    Conta registrarPagamentoParcial(Long id, Double valorPago);
}
