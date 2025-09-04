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

import tecstock_spring.model.Servico;
import tecstock_spring.service.ServicoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServicoController {

    private final ServicoService service;
    Logger logger = Logger.getLogger(ServicoController.class);

    @PostMapping("/api/servicos/salvar")
    public Servico salvar(@RequestBody Servico servico) {
        logger.info("Salvando serviço: " + servico + " no controller.");
        return service.salvar(servico);
    }

    @GetMapping("/api/servicos/buscar/{id}")
    public Servico buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/api/servicos/listarTodos")
    public List<Servico> listarTodos() {
        logger.info("Listando serviços no controller.");
        return service.listarTodos();
    }

    @PutMapping("/api/servicos/atualizar/{id}")
    public Servico atualizar(@PathVariable Long id, @RequestBody Servico servico) {
        logger.info("Atualizando serviço no controller. ID: " + id + ", Serviço: " + servico);
        return service.atualizar(id, servico);
    }

    @DeleteMapping("/api/servicos/deletar/{id}")
    public void deletar(@PathVariable Long id) {
        logger.info("Deletando serviço no controller. ID: " + id);
        service.deletar(id);
    }
}