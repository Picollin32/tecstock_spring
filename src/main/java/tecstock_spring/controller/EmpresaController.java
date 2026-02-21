package tecstock_spring.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tecstock_spring.model.Empresa;
import tecstock_spring.service.EmpresaService;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/empresas")
public class EmpresaController {
    
    private final EmpresaService service;
    private static final Logger logger = LoggerFactory.getLogger(EmpresaController.class);
    
    @PostMapping("/salvar")
    @PreAuthorize("hasAuthority('NIVEL_ACESSO_0')")
    public ResponseEntity<Map<String, Object>> salvar(@Valid @RequestBody Empresa empresa) {
        logger.info("Salvando empresa: " + empresa.getRazaoSocial());
        Map<String, Object> response = new HashMap<>();
        
        try {
            Empresa empresaSalva = service.salvar(empresa);
            response.put("success", true);
            response.put("message", "Empresa cadastrada com sucesso");
            response.put("data", empresaSalva);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Erro ao salvar empresa", e);
            response.put("success", false);
            response.put("message", "Erro ao salvar empresa: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/buscar/{id}")
    @PreAuthorize("hasAuthority('NIVEL_ACESSO_0')")
    public ResponseEntity<Empresa> buscarPorId(@PathVariable Long id) {
        logger.info("Buscando empresa por ID: " + id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }
    
    @GetMapping("/buscar-cnpj/{cnpj}")
    @PreAuthorize("hasAuthority('NIVEL_ACESSO_0')")
    public ResponseEntity<Empresa> buscarPorCnpj(@PathVariable String cnpj) {
        logger.info("Buscando empresa por CNPJ: " + cnpj);
        return ResponseEntity.ok(service.buscarPorCnpj(cnpj));
    }
    
    @GetMapping("/listarTodas")
    @PreAuthorize("hasAuthority('NIVEL_ACESSO_0')")
    public ResponseEntity<List<Empresa>> listarTodas() {
        logger.info("Listando todas as empresas");
        return ResponseEntity.ok(service.listarTodas());
    }
    
    @PutMapping("/atualizar/{id}")
    @PreAuthorize("hasAuthority('NIVEL_ACESSO_0')")
    public ResponseEntity<Map<String, Object>> atualizar(@PathVariable Long id, @Valid @RequestBody Empresa empresa) {
        logger.info("Atualizando empresa ID: " + id);
        Map<String, Object> response = new HashMap<>();
        
        try {
            Empresa empresaAtualizada = service.atualizar(id, empresa);
            response.put("success", true);
            response.put("message", "Empresa atualizada com sucesso");
            response.put("data", empresaAtualizada);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Erro ao atualizar empresa", e);
            response.put("success", false);
            response.put("message", "Erro ao atualizar empresa: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @DeleteMapping("/deletar/{id}")
    @PreAuthorize("hasAuthority('NIVEL_ACESSO_0')")
    public ResponseEntity<Map<String, Object>> deletar(@PathVariable Long id) {
        logger.info("Deletando empresa ID: " + id);
        Map<String, Object> response = new HashMap<>();
        
        try {
            service.deletar(id);
            response.put("success", true);
            response.put("message", "Empresa deletada com sucesso");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro ao deletar empresa", e);
            response.put("success", false);
            response.put("message", "Erro ao deletar empresa: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/buscarPaginado")
    @PreAuthorize("hasAuthority('NIVEL_ACESSO_0')")
    public ResponseEntity<?> buscarPaginado(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        logger.info("Buscando empresas paginado. Query: " + query + ", page: " + page);
        if (query == null || query.trim().isEmpty()) {
            List<Empresa> lista = service.listarUltimosParaInicio(6);
            Map<String, Object> response = new HashMap<>();
            response.put("content", lista);
            response.put("totalElements", lista.size());
            response.put("totalPages", 1);
            response.put("number", 0);
            return ResponseEntity.ok(response);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Empresa> resultado = service.buscarPaginado(query, pageable);
        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/ativar-desativar/{id}")
    @PreAuthorize("hasAuthority('NIVEL_ACESSO_0')")
    public ResponseEntity<Map<String, Object>> ativarDesativar(@PathVariable Long id, @RequestParam Boolean ativa) {
        logger.info("Alterando status da empresa ID: " + id);
        Map<String, Object> response = new HashMap<>();
        
        try {
            service.ativarDesativar(id, ativa);
            response.put("success", true);
            response.put("message", "Status da empresa alterado com sucesso");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro ao alterar status da empresa", e);
            response.put("success", false);
            response.put("message", "Erro ao alterar status: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
