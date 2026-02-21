package tecstock_spring.controller;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tecstock_spring.model.Usuario;
import tecstock_spring.dto.UsuarioResponseDTO;
import tecstock_spring.service.UsuarioService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;
    Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @PostMapping("/api/usuarios/salvar")
    public ResponseEntity<?> salvar(@Valid @RequestBody Usuario usuario) {
        logger.info("Salvando novo usuario no controller.");
        Usuario usuarioSalvo = service.salvar(usuario);
        return ResponseEntity.ok(convertToDTO(usuarioSalvo));
    }

    @GetMapping("/api/usuarios/buscar/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        logger.info("Buscando usuario por ID: " + id);
        Usuario usuario = service.buscarPorId(id);
        return ResponseEntity.ok(convertToDTO(usuario));
    }

    @GetMapping("/api/usuarios/listarTodos")
    public ResponseEntity<?> listarTodos() {
        logger.info("Listando todos os usuarios no controller.");
        List<Usuario> usuarios = service.listarTodos();
        return ResponseEntity.ok(usuarios.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    @PutMapping("/api/usuarios/atualizar/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        logger.info("Atualizando usuario no controller. ID: " + id);
        Usuario usuarioAtualizado = service.atualizar(id, usuario);
        return ResponseEntity.ok(convertToDTO(usuarioAtualizado));
    }

    @GetMapping("/api/usuarios/buscarPaginado")
    public ResponseEntity<?> buscarPaginado(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        logger.info("Buscando usuarios paginado. Query: " + query + ", page: " + page);
        if (query == null || query.trim().isEmpty()) {
            List<Usuario> lista = service.listarUltimosParaInicio(6);
            return ResponseEntity.ok(new java.util.HashMap<String, Object>() {{
                put("content", lista.stream().map(u -> convertToDTO(u)).collect(java.util.stream.Collectors.toList()));
                put("totalElements", lista.size());
                put("totalPages", 1);
                put("number", 0);
            }});
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Usuario> resultado = service.buscarPaginado(query, pageable);
        return ResponseEntity.ok(resultado.map(this::convertToDTO));
    }

    @DeleteMapping("/api/usuarios/deletar/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        logger.info("Deletando usuario no controller. ID: " + id);
        service.deletar(id);
        return ResponseEntity.ok().build();
    }
    
    private UsuarioResponseDTO convertToDTO(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nomeUsuario(usuario.getNomeUsuario())
                .nivelAcesso(usuario.getNivelAcesso())
                .consultor(usuario.getConsultor())
                .empresa(usuario.getEmpresa())
                .createdAt(usuario.getCreatedAt())
                .updatedAt(usuario.getUpdatedAt())
                .build();
    }
}
