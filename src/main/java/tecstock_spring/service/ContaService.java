package tecstock_spring.service;

import tecstock_spring.dto.ContaComParcelasDTO;
import tecstock_spring.dto.ContaParcelaDTO;
import tecstock_spring.dto.ContaParcelaEdicaoDTO;
import tecstock_spring.model.Conta;
import tecstock_spring.model.OrdemServico;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ContaService {

    void gerarContasParaOS(OrdemServico ordemServico);

    void gerarContasParaCompra(Map<String, Object> dadosPagamento, double valorTotal, String descricaoBase);

    List<Conta> listarPorMesAno(int mes, int ano);

    List<Conta> listarAPagarPorMesAno(int mes, int ano);

    List<Conta> listarAReceberPorMesAno(int mes, int ano);

    List<Conta> listarAtrasadas();

    Conta adicionarContaPagar(Conta conta);

    Conta marcarComoPago(Long id, LocalDate dataPagamento, Double acrescimo, Double desconto);

    Conta desmarcarPagamento(Long id);

    void deletar(Long id);

    Conta editar(Long id, Map<String, Object> dados);

    void removerContasDaOS(Long osId);

    Map<String, Double> resumoMes(int mes, int ano);

    Conta registrarPagamentoParcial(Long id, Double valorPago);

    List<Conta> buscarContasCompra(String numeroNota);

    void atualizarNumeroNotaEmContas(String antigoNumero, String novoNumero);

    void deletarContasCompra(String numeroNota);

    ContaComParcelasDTO buscarContaComParcelas(Long contaId);

    List<ContaParcelaDTO> listarParcelasDaConta(Long contaId);

    ContaParcelaDTO marcarParcelaComoPaga(Long parcelaId, LocalDate dataPagamento, Double acrescimo, Double desconto);

    ContaParcelaDTO desmarcarPagamentoParcela(Long parcelaId);

    ContaParcelaDTO editarParcela(Long parcelaId, ContaParcelaEdicaoDTO dados);

    void deletarParcela(Long parcelaId);
}
