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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tecstock_spring.model.Peca;
import tecstock_spring.service.PecaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pecas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PecaController {

    private final PecaService service;
    private static final Logger logger = Logger.getLogger(PecaController.class);

    @PostMapping("/salvar")
    public Peca salvar(@RequestBody Peca peca) {
        logger.info("Salvando peça: " + peca);
        return service.salvar(peca);
    }

    @GetMapping("/buscar/{id}")
    public Peca buscarPorId(@PathVariable Long id) {
        logger.info("Buscando peça com ID: " + id);
        return service.buscarPorId(id);
    }

    @GetMapping("/buscarPorCodigo/{codigo}")
    public Peca buscarPorCodigo(@PathVariable String codigo) {
        logger.info("Buscando peça com código: " + codigo);
        return service.buscarPorCodigo(codigo);
    }

    @GetMapping("/listarTodas")
    public List<Peca> listarTodos() {
        logger.info("Listando todas as peças.");
        return service.listarTodas();
    }

    @PutMapping("/atualizar/{id}")
    public Peca atualizar(@PathVariable Long id, @RequestBody Peca peca) {
        logger.info("Atualizando peça com ID: " + id);
        return service.atualizar(id, peca);
    }

    @DeleteMapping("/deletar/{id}")
    public void deletar(@PathVariable Long id) {
        logger.info("Deletando peça com ID: " + id);
        service.deletar(id);
    }
}