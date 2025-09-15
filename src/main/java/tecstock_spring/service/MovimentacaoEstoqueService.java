package tecstock_spring.service;

import tecstock_spring.model.MovimentacaoEstoque;
import java.util.List;

public interface MovimentacaoEstoqueService {
    
    // Adicionado parâmetro opcional 'origem' para identificar quem está solicitando a movimentação
    MovimentacaoEstoque registrarEntrada(String codigoPeca, Long fornecedorId, int quantidade, Double precoUnitario, String numeroNotaFiscal, String observacoes, String origem);

    MovimentacaoEstoque registrarSaida(String codigoPeca, Long fornecedorId, int quantidade, String numeroNotaFiscal, String observacoes, String origem);

    // Métodos legados para compatibilidade - delegam para a nova assinatura com origem = null
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
}
