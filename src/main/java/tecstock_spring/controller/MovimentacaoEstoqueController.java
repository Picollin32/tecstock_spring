package tecstock_spring.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tecstock_spring.model.MovimentacaoEstoque;
import tecstock_spring.service.MovimentacaoEstoqueService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/movimentacao-estoque")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MovimentacaoEstoqueController {

    private final MovimentacaoEstoqueService service;
    private static final Logger logger = Logger.getLogger(MovimentacaoEstoqueController.class);

    @PostMapping("/entrada")
    public MovimentacaoEstoque registrarEntrada(
            @RequestParam String codigoPeca,
            @RequestParam Long fornecedorId,
            @RequestParam int quantidade,
            @RequestParam Double precoUnitario,
            @RequestParam String numeroNotaFiscal,
            @RequestParam(required = false) String observacoes,
            @RequestParam(required = false) String origem) {
        logger.info("Registrando entrada - Código: " + codigoPeca + ", Fornecedor: " + fornecedorId + ", Quantidade: " + quantidade + ", Preço: " + precoUnitario + ", Nota: " + numeroNotaFiscal + ", Origem: " + origem);
        return service.registrarEntrada(codigoPeca, fornecedorId, quantidade, precoUnitario, numeroNotaFiscal, observacoes, origem);
    }

    @PostMapping("/saida")
    public MovimentacaoEstoque registrarSaida(
            @RequestParam String codigoPeca,
            @RequestParam Long fornecedorId,
            @RequestParam int quantidade,
            @RequestParam(required = false) String numeroNotaFiscal,
            @RequestParam(required = false) String observacoes,
            @RequestParam(required = false) String origem) {
        logger.info("Registrando saída - Código: " + codigoPeca + ", Fornecedor: " + fornecedorId + ", Quantidade: " + quantidade + ", Nota: " + numeroNotaFiscal + ", Origem: " + origem);
        return service.registrarSaida(codigoPeca, fornecedorId, quantidade, numeroNotaFiscal, observacoes, origem);
    }

    @PostMapping("/entrada-multipla")
    public Map<String, Object> registrarEntradasMultiplas(@RequestBody Map<String, Object> dadosEntrada) {
        logger.info("Registrando múltiplas entradas de estoque");
        
        try {
            Long fornecedorId = Long.valueOf(dadosEntrada.get("fornecedorId").toString());
            String numeroNotaFiscal = dadosEntrada.get("numeroNotaFiscal").toString();
            String observacoes = dadosEntrada.get("observacoes") != null ? dadosEntrada.get("observacoes").toString() : null;
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> pecas = (List<Map<String, Object>>) dadosEntrada.get("pecas");
            
            List<String> resultados = new ArrayList<>();
            int sucessos = 0;
            int falhas = 0;
            
            if (service.verificarNotaFiscalJaUtilizada(numeroNotaFiscal, fornecedorId)) {
                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", false);
                response.put("mensagem", "O número da nota fiscal '" + numeroNotaFiscal + "' já foi utilizado para este fornecedor.");
                return response;
            }
            
            for (Map<String, Object> peca : pecas) {
                try {
                    String codigoPeca = peca.get("codigoPeca").toString();
                    int quantidade = Integer.parseInt(peca.get("quantidade").toString());
                    Double precoUnitario = Double.parseDouble(peca.get("precoUnitario").toString());
                    
                    service.registrarEntradaSemValidacaoNota(
                        codigoPeca, fornecedorId, quantidade, precoUnitario, numeroNotaFiscal, observacoes
                    );
                    
                    sucessos++;
                    resultados.add("✓ " + codigoPeca + ": Entrada registrada com sucesso");
                    
                } catch (Exception e) {
                    falhas++;
                    resultados.add("✗ " + peca.get("codigoPeca") + ": " + e.getMessage());
                    logger.error("Erro ao registrar peça " + peca.get("codigoPeca") + ": " + e.getMessage());
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", sucessos > 0);
            response.put("sucessos", sucessos);
            response.put("falhas", falhas);
            response.put("resultados", resultados);
            response.put("mensagem", sucessos + " peças registradas, " + falhas + " falharam");
            
            return response;
            
        } catch (Exception e) {
            logger.error("Erro ao processar entradas múltiplas: " + e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao processar entrada: " + e.getMessage());
            return response;
        }
    }

    @GetMapping("/listar")
    public List<MovimentacaoEstoque> listarTodas() {
        logger.info("Listando todas as movimentações de estoque.");
        return service.listarTodas();
    }

    @GetMapping("/buscar/{id}")
    public MovimentacaoEstoque buscarPorId(@PathVariable Long id) {
        logger.info("Buscando movimentação com ID: " + id);
        return service.buscarPorId(id);
    }

    @GetMapping("/por-codigo/{codigoPeca}")
    public List<MovimentacaoEstoque> listarPorCodigoPeca(@PathVariable String codigoPeca) {
        logger.info("Listando movimentações para o código: " + codigoPeca);
        return service.listarPorCodigoPeca(codigoPeca);
    }

    @GetMapping("/por-fornecedor/{fornecedorId}")
    public List<MovimentacaoEstoque> listarPorFornecedor(@PathVariable Long fornecedorId) {
        logger.info("Listando movimentações para o fornecedor ID: " + fornecedorId);
        return service.listarPorFornecedor(fornecedorId);
    }
}
