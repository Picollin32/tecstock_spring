package tecstock_spring.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService service;
    Logger logger = Logger.getLogger(UsuarioController.class);

    @PostMapping("/api/usuarios/salvar")
    public UsuarioResponseDTO salvar(@RequestBody Usuario usuario) {
        logger.info("Salvando usuario: " + usuario.getNomeUsuario() + " no controller.");
        Usuario usuarioSalvo = service.salvar(usuario);
        return convertToDTO(usuarioSalvo);
    }

    @GetMapping("/api/usuarios/buscar/{id}")
    public UsuarioResponseDTO buscarPorId(@PathVariable Long id) {
        Usuario usuario = service.buscarPorId(id);
        return convertToDTO(usuario);
    }

    @GetMapping("/api/usuarios/listarTodos")
    public List<UsuarioResponseDTO> listarTodos() {
        logger.info("Listando usuarios no controller.");
        List<Usuario> usuarios = service.listarTodos();
        return usuarios.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/api/usuarios/atualizar/{id}")
    public UsuarioResponseDTO atualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        logger.info("Atualizando usuario no controller. ID: " + id + ", usuario: " + usuario.getNomeUsuario());
        Usuario usuarioAtualizado = service.atualizar(id, usuario);
        return convertToDTO(usuarioAtualizado);
    }

    @DeleteMapping("/api/usuarios/deletar/{id}")
    public void deletar(@PathVariable Long id) {
        logger.info("Deletando usuario no controller. ID: " + id);
        service.deletar(id);
    }
    
    private UsuarioResponseDTO convertToDTO(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nomeUsuario(usuario.getNomeUsuario())
                .consultor(usuario.getConsultor())
                .createdAt(usuario.getCreatedAt())
                .updatedAt(usuario.getUpdatedAt())
                .build();
    }
}
