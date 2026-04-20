package tecstock_spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tecstock_spring.model.CategoriaFinanceira;
import tecstock_spring.service.CategoriaFinanceiraService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categorias-financeiras")
public class CategoriaFinanceiraController {

    private final CategoriaFinanceiraService categoriaService;

    @GetMapping
    public List<CategoriaFinanceira> listar() {
        return categoriaService.listarAtivas();
    }

    @GetMapping("/buscarPaginado")
    public Map<String, Object> buscarPaginado(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CategoriaFinanceira> resultado = categoriaService.buscarPaginado(query, pageable);

        return Map.of(
                "content", resultado.getContent(),
                "totalElements", resultado.getTotalElements(),
                "totalPages", resultado.getTotalPages(),
                "number", resultado.getNumber());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody CategoriaFinanceira categoria) {
        try {
            CategoriaFinanceira criada = categoriaService.criar(categoria);
            return ResponseEntity.status(HttpStatus.CREATED).body(criada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody CategoriaFinanceira categoria) {
        try {
            CategoriaFinanceira atualizada = categoriaService.atualizar(id, categoria);
            return ResponseEntity.ok(atualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            categoriaService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }
}
