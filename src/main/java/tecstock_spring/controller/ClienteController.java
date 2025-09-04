package tecstock_spring.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import tecstock_spring.model.Cliente;
import tecstock_spring.service.ClienteService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService service;
    Logger logger = Logger.getLogger(ClienteController.class);

    @PostMapping("/api/clientes/salvar")
    public Cliente salvar(@RequestBody Cliente cliente) {
        logger.info("Salvando cliente: " + cliente + " no controller.");
        return service.salvar(cliente);
    }

    @GetMapping("/api/clientes/buscar/{id}")
    public Cliente buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/api/clientes/listarTodos")
    public List<Cliente> listarTodos() {
        logger.info("Listando clientes no controller.");
        return service.listarTodos();
    }

    @PutMapping("/api/clientes/atualizar/{id}")
    public Cliente atualizar(@PathVariable Long id, @RequestBody Cliente cliente) {
        logger.info("Atualizando cliente no controller. ID: " + id + ", Cliente: " + cliente);
        return service.atualizar(id, cliente);
    }

    @DeleteMapping("/api/clientes/deletar/{id}")
    public void deletar(@PathVariable Long id) {
        logger.info("Deletando cliente no controller. ID: " + id);
        service.deletar(id);
    }
}
