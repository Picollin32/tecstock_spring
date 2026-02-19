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

import tecstock_spring.model.Fabricante;
import tecstock_spring.service.FabricanteService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FabricanteController {

    private final FabricanteService service;
    Logger logger = LoggerFactory.getLogger(FabricanteController.class);

    @PostMapping("/api/fabricantes/salvar")
    public Fabricante salvar(@RequestBody Fabricante fabricante) {
        logger.info("Salvando fabricante: " + fabricante + " no controller.");
        return service.salvar(fabricante);
    }

    @GetMapping("/api/fabricantes/buscar/{id}")
    public Fabricante buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/api/fabricantes/listarTodos")
    public List<Fabricante> listarTodos() {
        logger.info("Listando fabricantes no controller.");
        return service.listarTodos();
    }

    @PutMapping("/api/fabricantes/atualizar/{id}")
    public Fabricante atualizar(@PathVariable Long id, @RequestBody Fabricante fabricante) {
        logger.info("Atualizando fabricante no controller. ID: " + id + ", fabricante: " + fabricante);
        return service.atualizar(id, fabricante);
    }

    @DeleteMapping("/api/fabricantes/deletar/{id}")
    public void deletar(@PathVariable Long id) {
        logger.info("Deletando fabricante no controller. ID: " + id);
        service.deletar(id);
    }
    
    @GetMapping("/api/fabricantes/buscarPaginado")
    public Object buscarPaginado(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        if (query == null || query.trim().isEmpty()) {
            List<tecstock_spring.dto.FabricantePesquisaDTO> lista = service.listarUltimosParaInicio(6);
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("content", lista);
            response.put("totalElements", lista.size());
            response.put("totalPages", 1);
            response.put("number", 0);
            return response;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return service.buscarPaginado(query, pageable);
    }
}
