package tecstock_spring.service;

import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tecstock_spring.exception.UsuarioDuplicadoException;
import tecstock_spring.model.Usuario;
import tecstock_spring.model.Funcionario;
import tecstock_spring.repository.UsuarioRepository;
import tecstock_spring.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;
    private final FuncionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder;
    Logger logger = Logger.getLogger(UsuarioServiceImpl.class);

    @Override
    public Usuario salvar(Usuario usuario) {
        validarNomeUsuarioDuplicado(usuario.getNomeUsuario(), null);

        if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
            throw new RuntimeException("Senha é obrigatória");
        }

        if (usuario.getNivelAcesso() == null) {
            throw new RuntimeException("Nível de acesso não informado");
        }
        
        if (usuario.getNivelAcesso() != 0 && usuario.getNivelAcesso() != 1) {
            throw new RuntimeException("Nível de acesso inválido. Use 0 (Admin) ou 1 (Consultor)");
        }
        
        // Consultor (nivelAcesso = 1) deve obrigatoriamente ter consultor atrelado
        if (usuario.getNivelAcesso() == 1) {
            if (usuario.getConsultor() == null || usuario.getConsultor().getId() == null) {
                throw new RuntimeException("Usuário do tipo Consultor deve ter um consultor atrelado");
            }
            
            Funcionario consultor = funcionarioRepository.findById(usuario.getConsultor().getId())
                    .orElseThrow(() -> new RuntimeException("Consultor não encontrado"));
            
            if (consultor.getNivelAcesso() != 1) {
                throw new RuntimeException("O funcionário informado não é um consultor");
            }
            
            usuario.setConsultor(consultor);
        }
        // Admin (nivelAcesso = 0) pode não ter consultor atrelado

        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);
        
        Usuario usuarioSalvo = repository.save(usuario);
        logger.info("Usuario salvo com sucesso: " + usuarioSalvo);
        return usuarioSalvo;
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado"));
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = repository.findAll();
        if (usuarios != null && usuarios.isEmpty()) {
            logger.info("Nenhum usuario cadastrado: " + usuarios);
            System.out.println("Nenhum usuario cadastrado: " + usuarios);
        } else if (usuarios != null && !usuarios.isEmpty()) {
            logger.info(usuarios.size() + " usuarios encontrados.");
            System.out.println(usuarios.size() + " usuarios encontrados.");
        }
        return usuarios;
    }

    @Override
    public Usuario atualizar(Long id, Usuario novoUsuario) {
        Usuario usuarioExistente = buscarPorId(id);
        validarNomeUsuarioDuplicado(novoUsuario.getNomeUsuario(), id);

        if (novoUsuario.getNivelAcesso() != null) {
            if (novoUsuario.getNivelAcesso() != 0 && novoUsuario.getNivelAcesso() != 1) {
                throw new RuntimeException("Nível de acesso inválido. Use 0 (Admin) ou 1 (Consultor)");
            }
            usuarioExistente.setNivelAcesso(novoUsuario.getNivelAcesso());
        }

        // Consultor (nivelAcesso = 1) deve obrigatoriamente ter consultor atrelado
        Integer nivelAcessoFinal = novoUsuario.getNivelAcesso() != null ? novoUsuario.getNivelAcesso() : usuarioExistente.getNivelAcesso();
        
        if (nivelAcessoFinal == 1) {
            if (novoUsuario.getConsultor() == null || novoUsuario.getConsultor().getId() == null) {
                if (usuarioExistente.getConsultor() == null) {
                    throw new RuntimeException("Usuário do tipo Consultor deve ter um consultor atrelado");
                }
            } else {
                Funcionario consultor = funcionarioRepository.findById(novoUsuario.getConsultor().getId())
                        .orElseThrow(() -> new RuntimeException("Consultor não encontrado"));
                
                if (consultor.getNivelAcesso() != 1) {
                    throw new RuntimeException("O funcionário informado não é um consultor");
                }
                
                usuarioExistente.setConsultor(consultor);
            }
        } else if (novoUsuario.getConsultor() != null && novoUsuario.getConsultor().getId() != null) {
            // Admin pode ter consultor atrelado opcionalmente
            Funcionario consultor = funcionarioRepository.findById(novoUsuario.getConsultor().getId())
                    .orElseThrow(() -> new RuntimeException("Consultor não encontrado"));
            
            if (consultor.getNivelAcesso() != 1) {
                throw new RuntimeException("O funcionário informado não é um consultor");
            }
            
            usuarioExistente.setConsultor(consultor);
        }

        if (novoUsuario.getSenha() != null && !novoUsuario.getSenha().isEmpty()) {
            String senhaCriptografada = passwordEncoder.encode(novoUsuario.getSenha());
            usuarioExistente.setSenha(senhaCriptografada);
        }

        usuarioExistente.setNomeUsuario(novoUsuario.getNomeUsuario());
        
        if (novoUsuario.getNomeCompleto() != null) {
            usuarioExistente.setNomeCompleto(novoUsuario.getNomeCompleto());
        }
        
        return repository.save(usuarioExistente);
    }

    @Override
    public void deletar(Long id) {
        repository.deleteById(id);
    }
    
    private void validarNomeUsuarioDuplicado(String nomeUsuario, Long idExcluir) {
        logger.info("Validando Nome de Usuario duplicado (ID excluindo: " + idExcluir + ")");
        
        if (nomeUsuario == null || nomeUsuario.trim().isEmpty()) {
            logger.warn("Nome de Usuario é nulo ou vazio");
            return;
        }
        
        boolean exists;
        if (idExcluir != null) {
            exists = repository.existsByNomeUsuarioAndIdNot(nomeUsuario, idExcluir);
            logger.info("Verificação para atualização - Existe outro usuario (excluindo ID " + idExcluir + "): " + exists);
        } else {
            exists = repository.existsByNomeUsuario(nomeUsuario);
            logger.info("Verificação para criação - Existe usuario: " + exists);
        }
        
        if (exists) {
            String mensagem = "Nome de usuário já cadastrado";
            logger.error(mensagem);
            throw new UsuarioDuplicadoException(mensagem);
        }
        
        logger.info("Validação de Nome de Usuario concluída com sucesso");
    }
}
