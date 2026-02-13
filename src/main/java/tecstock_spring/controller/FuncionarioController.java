package tecstock_spring.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tecstock_spring.model.Funcionario;
import tecstock_spring.service.FuncionarioService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FuncionarioController {

    private final FuncionarioService service;
    Logger logger = LoggerFactory.getLogger(FuncionarioController.class);

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

    @GetMapping("/api/funcionarios/listarMecanicos")
    public List<Funcionario> listarMecanicos() {
        logger.info("Listando mecânicos no controller.");
        return service.listarMecanicos();
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
    
    @GetMapping("/api/funcionarios/buscarPaginado")
    public Object buscarPaginado(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        // Se não há pesquisa, retorna os últimos 6 cadastros sem paginação
        if (query == null || query.trim().isEmpty()) {
            List<tecstock_spring.dto.FuncionarioPesquisaDTO> lista = service.listarUltimosParaInicio(6);
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("content", lista);
            response.put("totalElements", lista.size());
            response.put("totalPages", 1);
            response.put("number", 0);
            return response;
        }
        // Com pesquisa, usa paginação normal
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return service.buscarPaginado(query, pageable);
    }
}
