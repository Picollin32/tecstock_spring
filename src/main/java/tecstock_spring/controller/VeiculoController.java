package tecstock_spring.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
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

import tecstock_spring.model.Veiculo;
import tecstock_spring.service.VeiculoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VeiculoController {

    private final VeiculoService service;
    Logger logger = LoggerFactory.getLogger(VeiculoController.class);

    @PostMapping("/api/veiculos/salvar")
    public Veiculo salvar(@RequestBody Veiculo veiculo) {
        logger.info("Salvando veículo: " + veiculo + " no controller.");
        return service.salvar(veiculo);
    }

    @GetMapping("/api/veiculos/buscar/{id}")
    public Veiculo buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/api/veiculos/listarTodos")
    public List<Veiculo> listarTodos() {
        logger.info("Listando veículos no controller.");
        return service.listarTodos();
    }

    @PutMapping("/api/veiculos/atualizar/{id}")
    public Veiculo atualizar(@PathVariable Long id, @RequestBody Veiculo veiculo) {
        logger.info("Atualizando veículo no controller. ID: " + id + ", Veículo: " + veiculo);
        return service.atualizar(id, veiculo);
    }

    @DeleteMapping("/api/veiculos/deletar/{id}")
    public void deletar(@PathVariable Long id) {
        logger.info("Deletando veículo no controller. ID: " + id);
        service.deletar(id);
    }
    
    @GetMapping("/api/veiculos/buscarPaginado")
    public Page<tecstock_spring.dto.VeiculoPesquisaDTO> buscarPaginado(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return service.buscarPaginado(query, pageable);
    }
}
