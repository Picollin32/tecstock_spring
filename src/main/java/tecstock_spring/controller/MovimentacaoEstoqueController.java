package tecstock_spring.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
            @RequestParam String numeroNotaFiscal,
            @RequestParam(required = false) String observacoes) {
        logger.info("Registrando entrada - Código: " + codigoPeca + ", Fornecedor: " + fornecedorId + ", Quantidade: " + quantidade + ", Nota: " + numeroNotaFiscal);
        return service.registrarEntrada(codigoPeca, fornecedorId, quantidade, numeroNotaFiscal, observacoes);
    }

    @PostMapping("/saida")
    public MovimentacaoEstoque registrarSaida(
            @RequestParam String codigoPeca,
            @RequestParam Long fornecedorId,
            @RequestParam int quantidade,
            @RequestParam String numeroNotaFiscal,
            @RequestParam(required = false) String observacoes) {
        logger.info("Registrando saída - Código: " + codigoPeca + ", Fornecedor: " + fornecedorId + ", Quantidade: " + quantidade + ", Nota: " + numeroNotaFiscal);
        return service.registrarSaida(codigoPeca, fornecedorId, quantidade, numeroNotaFiscal, observacoes);
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
