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

import tecstock_spring.model.Checklist;
import tecstock_spring.service.ChecklistService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChecklistController {

    private final ChecklistService service;
    Logger logger = Logger.getLogger(ChecklistController.class);

    @PostMapping("/api/checklists/salvar")
    public Checklist salvar(@RequestBody Checklist checklist) {
        logger.info("Salvando checklist: " + checklist + " no controller.");
        return service.salvar(checklist);
    }

    @GetMapping("/api/checklists/buscar/{id}")
    public Checklist buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/api/checklists/listarTodos")
    public List<Checklist> listarTodos() {
        logger.info("Listando checklists no controller.");
        return service.listarTodos();
    }

    @PutMapping("/api/checklists/atualizar/{id}")
    public Checklist atualizar(@PathVariable Long id, @RequestBody Checklist checklist) {
        logger.info("Atualizando checklist no controller. ID: " + id + ", Checklists: " + checklist);
        return service.atualizar(id, checklist);
    }

    @DeleteMapping("/api/checklists/deletar/{id}")
    public void deletar(@PathVariable Long id) {
        logger.info("Deletando checklist no controller. ID: " + id);
        service.deletar(id);
    }
    
    @PutMapping("/api/checklists/fechar/{id}")
    public boolean fecharChecklist(@PathVariable Long id) {
        logger.info("Fechando checklist no controller. ID: " + id);
        return service.fecharChecklist(id);
    }
    
    @PutMapping("/api/checklists/reabrir/{id}")
    public boolean reabrirChecklist(@PathVariable Long id) {
        logger.info("Reabrindo checklist no controller. ID: " + id);
        return service.reabrirChecklist(id);
    }
}
