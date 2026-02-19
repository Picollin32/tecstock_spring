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

import tecstock_spring.model.Marca;
import tecstock_spring.service.MarcaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MarcaController {

    private final MarcaService service;
    Logger logger = LoggerFactory.getLogger(MarcaController.class);

    @PostMapping("/api/marcas/salvar")
    public Marca salvar(@RequestBody Marca marca) {
        logger.info("Salvando marca: " + marca + " no controller.");
        return service.salvar(marca);
    }

    @GetMapping("/api/marcas/buscar/{id}")
    public Marca buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/api/marcas/listarTodos")
    public List<Marca> listarTodos() {
        logger.info("Listando marcas no controller.");
        return service.listarTodos();
    }

    @PutMapping("/api/marcas/atualizar/{id}")
    public Marca atualizar(@PathVariable Long id, @RequestBody Marca marca) {
        logger.info("Atualizando marca no controller. ID: " + id + ", Marcas: " + marca);
        return service.atualizar(id, marca);
    }

    @DeleteMapping("/api/marcas/deletar/{id}")
    public void deletar(@PathVariable Long id) {
        logger.info("Deletando marca no controller. ID: " + id);
        service.deletar(id);
    }
    
    @GetMapping("/api/marcas/buscarPaginado")
    public Object buscarPaginado(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        if (query == null || query.trim().isEmpty()) {
            List<tecstock_spring.dto.MarcaPesquisaDTO> lista = service.listarUltimosParaInicio(6);
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
