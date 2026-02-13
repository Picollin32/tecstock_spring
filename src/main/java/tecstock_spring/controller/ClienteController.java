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

import tecstock_spring.model.Cliente;
import tecstock_spring.service.ClienteService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService service;
    Logger logger = LoggerFactory.getLogger(ClienteController.class);

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
    
    @GetMapping("/api/clientes/buscarPaginado")
    public Object buscarPaginado(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        // Se não há pesquisa, retorna os últimos 6 cadastros sem paginação
        if (query == null || query.trim().isEmpty()) {
            List<tecstock_spring.dto.ClientePesquisaDTO> lista = service.listarUltimosParaInicio(6);
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
