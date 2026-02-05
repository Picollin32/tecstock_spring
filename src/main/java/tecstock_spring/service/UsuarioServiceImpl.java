package tecstock_spring.service;

import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tecstock_spring.exception.UsuarioDuplicadoException;
import tecstock_spring.exception.ResourceNotFoundException;
import tecstock_spring.model.Usuario;
import tecstock_spring.model.Funcionario;
import tecstock_spring.model.Empresa;
import tecstock_spring.repository.UsuarioRepository;
import tecstock_spring.repository.FuncionarioRepository;
import tecstock_spring.repository.EmpresaRepository;
import tecstock_spring.util.TenantContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;
    private final FuncionarioRepository funcionarioRepository;
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;
    Logger logger = Logger.getLogger(UsuarioServiceImpl.class);

    @Override
    public Usuario salvar(Usuario usuario) {
 
        Empresa empresa;
        if (usuario.getEmpresa() != null && usuario.getEmpresa().getId() != null) {

            empresa = empresaRepository.findById(usuario.getEmpresa().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
        } else {

            Long empresaId = TenantContext.getCurrentEmpresaId();
            if (empresaId == null) {
                throw new RuntimeException("Empresa não encontrada no contexto do usuário");
            }
            empresa = empresaRepository.findById(empresaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
        }
        
        usuario.setEmpresa(empresa);
        
        validarNomeUsuarioDuplicado(usuario.getNomeUsuario(), null);

        if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
            throw new RuntimeException("Senha é obrigatória");
        }

        if (usuario.getNivelAcesso() == null) {
            throw new RuntimeException("Nível de acesso não informado");
        }
        
        if (usuario.getNivelAcesso() != 1 && usuario.getNivelAcesso() != 2) {
            throw new RuntimeException("Nível de acesso inválido. Use 1 (Admin) ou 2 (Consultor)");
        }

        if (usuario.getNivelAcesso() == 2) {
            if (usuario.getConsultor() == null || usuario.getConsultor().getId() == null) {
                throw new RuntimeException("Usuário do tipo Consultor deve ter um consultor atrelado");
            }
            
            Funcionario consultor = funcionarioRepository.findById(usuario.getConsultor().getId())
                    .orElseThrow(() -> new RuntimeException("Consultor não encontrado"));
            
            if (consultor.getNivelAcesso() != 2) {
                throw new RuntimeException("O funcionário informado não é um consultor");
            }
            
            usuario.setConsultor(consultor);
        }


        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);
        
        Usuario usuarioSalvo = repository.save(usuario);
        logger.info("Usuario salvo com sucesso: " + usuarioSalvo);
        return usuarioSalvo;
    }

    @Override
    public Usuario buscarPorId(Long id) {

        if (TenantContext.isSuperAdmin()) {
            return repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario não encontrado"));
        }

        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Empresa não encontrada no contexto do usuário");
        }
        
        return repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario não encontrado"));
    }

    @Override
    public List<Usuario> listarTodos() {

        if (TenantContext.isSuperAdmin()) {
            List<Usuario> usuarios = repository.findAll();
            logger.info(usuarios.size() + " usuarios encontrados (SuperAdmin).");
            return usuarios;
        }

        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Empresa não encontrada no contexto do usuário");
        }
        
        List<Usuario> usuarios = repository.findByEmpresaId(empresaId);
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

        if (!TenantContext.isSuperAdmin()) {
            Long empresaId = TenantContext.getCurrentEmpresaId();
            if (empresaId == null) {
                throw new RuntimeException("Empresa não encontrada no contexto do usuário");
            }
        }

        Usuario usuarioExistente = buscarPorId(id);
        validarNomeUsuarioDuplicado(novoUsuario.getNomeUsuario(), id);

        if (novoUsuario.getNivelAcesso() != null) {
            if (novoUsuario.getNivelAcesso() != 1 && novoUsuario.getNivelAcesso() != 2) {
                throw new RuntimeException("Nível de acesso inválido. Use 1 (Admin) ou 2 (Consultor)");
            }
            usuarioExistente.setNivelAcesso(novoUsuario.getNivelAcesso());
        }

        Integer nivelAcessoFinal = novoUsuario.getNivelAcesso() != null ? novoUsuario.getNivelAcesso() : usuarioExistente.getNivelAcesso();
        
        if (nivelAcessoFinal == 2) {
            if (novoUsuario.getConsultor() == null || novoUsuario.getConsultor().getId() == null) {
                if (usuarioExistente.getConsultor() == null) {
                    throw new RuntimeException("Usuário do tipo Consultor deve ter um consultor atrelado");
                }
            } else {
                Funcionario consultor = funcionarioRepository.findById(novoUsuario.getConsultor().getId())
                        .orElseThrow(() -> new RuntimeException("Consultor não encontrado"));
                
                if (consultor.getNivelAcesso() != 2) {
                    throw new RuntimeException("O funcionário informado não é um consultor");
                }
                
                usuarioExistente.setConsultor(consultor);
            }
        } else if (novoUsuario.getConsultor() != null && novoUsuario.getConsultor().getId() != null) {

            Funcionario consultor = funcionarioRepository.findById(novoUsuario.getConsultor().getId())
                    .orElseThrow(() -> new RuntimeException("Consultor não encontrado"));
            
            if (consultor.getNivelAcesso() != 2) {
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

        if (!TenantContext.isSuperAdmin()) {
            Long empresaId = TenantContext.getCurrentEmpresaId();
            if (empresaId == null) {
                throw new RuntimeException("Empresa não encontrada no contexto do usuário");
            }
        }

        Usuario usuario = buscarPorId(id);

        if (usuario.getNivelAcesso() == 1 && usuario.getEmpresa() != null) {
            Long empresaId = usuario.getEmpresa().getId();
            
            StringBuilder mensagemErro = new StringBuilder("Não é possível excluir este usuário administrador porque a empresa dele possui dados cadastrados:");
            boolean possuiDados = false;

            if (repository.existsOtherUsuariosInEmpresa(empresaId, id)) {
                mensagemErro.append("\n- Outros usuários");
                possuiDados = true;
            }
            
            if (empresaRepository.hasFuncionarios(empresaId)) {
                mensagemErro.append("\n- Funcionários");
                possuiDados = true;
            }
            
            if (empresaRepository.hasClientes(empresaId)) {
                mensagemErro.append("\n- Clientes");
                possuiDados = true;
            }
            
            if (empresaRepository.hasVeiculos(empresaId)) {
                mensagemErro.append("\n- Veículos");
                possuiDados = true;
            }
            
            if (empresaRepository.hasPecas(empresaId)) {
                mensagemErro.append("\n- Peças");
                possuiDados = true;
            }
            
            if (empresaRepository.hasServicos(empresaId)) {
                mensagemErro.append("\n- Serviços");
                possuiDados = true;
            }
            
            if (empresaRepository.hasOrdensServico(empresaId)) {
                mensagemErro.append("\n- Ordens de Serviço");
                possuiDados = true;
            }
            
            if (empresaRepository.hasOrcamentos(empresaId)) {
                mensagemErro.append("\n- Orçamentos");
                possuiDados = true;
            }
            
            if (empresaRepository.hasAgendamentos(empresaId)) {
                mensagemErro.append("\n- Agendamentos");
                possuiDados = true;
            }
            
            if (empresaRepository.hasChecklists(empresaId)) {
                mensagemErro.append("\n- Checklists");
                possuiDados = true;
            }
            
            if (empresaRepository.hasMovimentacoesEstoque(empresaId)) {
                mensagemErro.append("\n- Movimentações de Estoque");
                possuiDados = true;
            }
            
            if (empresaRepository.hasFornecedores(empresaId)) {
                mensagemErro.append("\n- Fornecedores");
                possuiDados = true;
            }
            
            if (empresaRepository.hasFabricantes(empresaId)) {
                mensagemErro.append("\n- Fabricantes");
                possuiDados = true;
            }
            
            if (empresaRepository.hasMarcas(empresaId)) {
                mensagemErro.append("\n- Marcas");
                possuiDados = true;
            }
            
            if (possuiDados) {
                throw new IllegalStateException(mensagemErro.toString());
            }
        }
        
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
