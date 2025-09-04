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

import tecstock_spring.model.Funcionario;
import tecstock_spring.service.FuncionarioService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FuncionarioController {

    private final FuncionarioService service;
    Logger logger = Logger.getLogger(FuncionarioController.class);

    @PostMapping("/api/funcionarios/salvar")
    public Funcionario salvar(@RequestBody Funcionario funcionario) {
        logger.info("Salvando funcionario: " + funcionario + " no controller.");
        return service.salvar(funcionario);
    }

    @GetMapping("/api/funcionarios/buscar/{id}")
    public Funcionario buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/api/funcionarios/listarTodos")
    public List<Funcionario> listarTodos() {
        logger.info("Listando funcionarios no controller.");
        return service.listarTodos();
    }

    @PutMapping("/api/funcionarios/atualizar/{id}")
    public Funcionario atualizar(@PathVariable Long id, @RequestBody Funcionario funcionario) {
        logger.info("Atualizando funcionario no controller. ID: " + id + ", funcionario: " + funcionario);
        return service.atualizar(id, funcionario);
    }

    @DeleteMapping("/api/funcionarios/deletar/{id}")
    public void deletar(@PathVariable Long id) {
        logger.info("Deletando funcionario no controller. ID: " + id);
        service.deletar(id);
    }
}
