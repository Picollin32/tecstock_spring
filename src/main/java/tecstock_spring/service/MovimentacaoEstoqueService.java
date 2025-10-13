package tecstock_spring.service;

import tecstock_spring.model.MovimentacaoEstoque;
import java.util.List;

public interface MovimentacaoEstoqueService {
    
    MovimentacaoEstoque registrarEntrada(String codigoPeca, Long fornecedorId, int quantidade, Double precoUnitario, String numeroNotaFiscal, String observacoes, String origem);

    MovimentacaoEstoque registrarSaida(String codigoPeca, Long fornecedorId, int quantidade, String numeroNotaFiscal, String observacoes, String origem);

    default MovimentacaoEstoque registrarEntrada(String codigoPeca, Long fornecedorId, int quantidade, Double precoUnitario, String numeroNotaFiscal, String observacoes) {
        return registrarEntrada(codigoPeca, fornecedorId, quantidade, precoUnitario, numeroNotaFiscal, observacoes, null);
    }

    default MovimentacaoEstoque registrarSaida(String codigoPeca, Long fornecedorId, int quantidade, String numeroNotaFiscal, String observacoes) {
        return registrarSaida(codigoPeca, fornecedorId, quantidade, numeroNotaFiscal, observacoes, null);
    }
    
    List<MovimentacaoEstoque> listarTodas();
    
    List<MovimentacaoEstoque> listarPorCodigoPeca(String codigoPeca);
    
    List<MovimentacaoEstoque> listarPorFornecedor(Long fornecedorId);
    
    MovimentacaoEstoque buscarPorId(Long id);
    
    void processarSaidaPorOrdemServico(String codigoPeca, Long fornecedorId, int quantidade, String numeroOS);
    
    void removerSaidasDeOrdemServico(String numeroOS);
    
    List<MovimentacaoEstoque> listarPorOrdemServico(String numeroOS);
    
    boolean verificarNotaFiscalJaUtilizada(String numeroNotaFiscal, Long fornecedorId);
    
    MovimentacaoEstoque registrarEntradaSemValidacaoNota(String codigoPeca, Long fornecedorId, int quantidade, Double precoUnitario, String numeroNotaFiscal, String observacoes);
}
