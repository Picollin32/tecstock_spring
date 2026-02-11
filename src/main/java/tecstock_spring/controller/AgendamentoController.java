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

import tecstock_spring.model.Agendamento;
import tecstock_spring.service.AgendamentoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AgendamentoController {

    private final AgendamentoService service;
    Logger logger = LoggerFactory.getLogger(AgendamentoController.class);

    @PostMapping("/api/agendamentos/salvar")
    public Agendamento salvar(@RequestBody Agendamento agendamento) {
        logger.info("Salvando agendamento: " + agendamento + " no controller.");
        return service.salvar(agendamento);
    }

    @GetMapping("/api/agendamentos/listarTodos")
    public List<Agendamento> listarTodos() {
        logger.info("Listando agendamentos no controller.");
        return service.listarTodos();
    }

    @PutMapping("/api/agendamentos/atualizar/{id}")
    public Agendamento atualizar(@PathVariable Long id, @RequestBody Agendamento agendamento) {
        logger.info("Atualizando agendamento ID: " + id + " com dados: " + agendamento);
        return service.atualizar(id, agendamento);
    }

    @DeleteMapping("/api/agendamentos/deletar/{id}")
    public void deletar(@PathVariable Long id) {
        logger.info("Deletando agendamento no controller. ID: " + id);
        service.deletar(id);
    }
}