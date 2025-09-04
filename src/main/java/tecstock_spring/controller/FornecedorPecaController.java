package tecstock_spring.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import tecstock_spring.dto.FornecedorPecaDTO;
import tecstock_spring.model.FornecedorPeca;
import tecstock_spring.model.FornecedorPecaId;
import tecstock_spring.service.FornecedorPecaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/descontos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FornecedorPecaController {

    private final FornecedorPecaService service;
    private static final Logger logger = Logger.getLogger(FornecedorPecaController.class);

    @PostMapping("/associar")
    public FornecedorPeca associarDesconto(@RequestBody FornecedorPecaDTO dto) {
        logger.info("Recebida requisição para associar desconto.");

        FornecedorPecaId id = new FornecedorPecaId(dto.getFornecedorId(), dto.getPecaId());
        FornecedorPeca fornecedorPeca = new FornecedorPeca();
        fornecedorPeca.setId(id);
        fornecedorPeca.setDesconto(dto.getDesconto());
        return service.salvar(fornecedorPeca);
    }

    @GetMapping("/buscar/{fornecedorId}/{pecaId}")
    public FornecedorPeca buscarAssociacao(@PathVariable Long fornecedorId, @PathVariable Long pecaId) {
        logger.info("Buscando associação para fornecedor " + fornecedorId + " e peça " + pecaId);
        FornecedorPecaId id = new FornecedorPecaId(fornecedorId, pecaId);
        return service.buscarPorId(id);
    }

    @GetMapping("/listarTodos")
    public List<FornecedorPeca> listarAssociacoes() {
        logger.info("Listando todas as associações de desconto.");
        return service.listarTodos();
    }
    
    @DeleteMapping("/remover/{fornecedorId}/{pecaId}")
    public void removerAssociacao(@PathVariable Long fornecedorId, @PathVariable Long pecaId) {
        logger.info("Removendo associação para fornecedor " + fornecedorId + " e peça " + pecaId);
        FornecedorPecaId id = new FornecedorPecaId(fornecedorId, pecaId);
        service.deletar(id);
    }
}