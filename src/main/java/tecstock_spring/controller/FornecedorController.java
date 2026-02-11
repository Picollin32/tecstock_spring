package tecstock_spring.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import tecstock_spring.model.Fornecedor;
import tecstock_spring.service.FornecedorService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FornecedorController {

    private final FornecedorService service;
    Logger logger = LoggerFactory.getLogger(FornecedorController.class);

    @PostMapping("/api/fornecedores/salvar")
    public Fornecedor salvar(@RequestBody Fornecedor fornecedor) {
        logger.info("Salvando fornecedor: " + fornecedor + " no controller.");
        return service.salvar(fornecedor);
    }

    @GetMapping("/api/fornecedores/buscar/{id}")
    public Fornecedor buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/api/fornecedores/listarTodos")
    public List<Fornecedor> listarTodos() {
        logger.info("Listando fornecedores no controller.");
        return service.listarTodos();
    }

    @PutMapping("/api/fornecedores/atualizar/{id}")
    public Fornecedor atualizar(@PathVariable Long id, @RequestBody Fornecedor fornecedor) {
        logger.info("Atualizando fornecedor no controller. ID: " + id + ", fornecedor: " + fornecedor);
        return service.atualizar(id, fornecedor);
    }

    @DeleteMapping("/api/fornecedores/deletar/{id}")
    public void deletar(@PathVariable Long id) {
        logger.info("Deletando fornecedor no controller. ID: " + id);
        service.deletar(id);
    }
}
