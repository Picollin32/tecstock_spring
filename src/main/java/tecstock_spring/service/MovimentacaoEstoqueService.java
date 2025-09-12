package tecstock_spring.service;

import tecstock_spring.model.MovimentacaoEstoque;
import java.util.List;

public interface MovimentacaoEstoqueService {
    
    MovimentacaoEstoque registrarEntrada(String codigoPeca, Long fornecedorId, int quantidade, Double precoUnitario, String numeroNotaFiscal, String observacoes);
    
    MovimentacaoEstoque registrarSaida(String codigoPeca, Long fornecedorId, int quantidade, String numeroNotaFiscal, String observacoes);
    
    List<MovimentacaoEstoque> listarTodas();
    
    List<MovimentacaoEstoque> listarPorCodigoPeca(String codigoPeca);
    
    List<MovimentacaoEstoque> listarPorFornecedor(Long fornecedorId);
    
    MovimentacaoEstoque buscarPorId(Long id);
}
