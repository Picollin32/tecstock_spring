package tecstock_spring.controller;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import tecstock_spring.model.Usuario;
import tecstock_spring.dto.UsuarioResponseDTO;
import tecstock_spring.service.UsuarioService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;
    Logger logger = Logger.getLogger(UsuarioController.class);

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
