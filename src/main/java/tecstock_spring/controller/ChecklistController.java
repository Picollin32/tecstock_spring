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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import tecstock_spring.dto.ChecklistPesquisaDTO;
import tecstock_spring.model.Checklist;
import tecstock_spring.service.ChecklistService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChecklistController {

    private final ChecklistService service;
    Logger logger = LoggerFactory.getLogger(ChecklistController.class);

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

    @GetMapping("/api/checklists/buscarPaginado")
    public Object buscarPaginado(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false, defaultValue = "numero") String tipo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (query == null || query.trim().isEmpty()) {
            List<Checklist> lista = service.listarUltimosParaInicio(5);
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("content", lista);
            response.put("totalElements", lista.size());
            response.put("totalPages", 1);
            response.put("number", 0);
            return response;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return service.buscarPaginado(query, tipo, pageable);
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
    
    @GetMapping("/api/checklists/pesquisar")
    public List<ChecklistPesquisaDTO> pesquisarPorNumeroExato(@RequestParam Integer numero) {
        logger.info("Pesquisando checklist com número exato: " + numero);
        List<Checklist> checklists = service.pesquisarPorNumeroExato(numero);
        return checklists.stream()
                .map(this::converterParaPesquisaDTO)
                .collect(java.util.stream.Collectors.toList());
    }
    
    private ChecklistPesquisaDTO converterParaPesquisaDTO(Checklist c) {
        return ChecklistPesquisaDTO.builder()
                .id(c.getId())
                .numeroChecklist(c.getNumeroChecklist())
                .createdAt(c.getCreatedAt())
                .clienteNome(c.getClienteNome())
                .clienteCpf(c.getClienteCpf())
                .veiculoNome(c.getVeiculoNome())
                .veiculoPlaca(c.getVeiculoPlaca())
                .status(c.getStatus())
                .consultorNome(c.getConsultor() != null ? c.getConsultor().getNome() : null)
                .build();
    }
}
