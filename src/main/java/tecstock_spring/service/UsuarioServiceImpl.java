package tecstock_spring.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tecstock_spring.exception.UsuarioDuplicadoException;
import tecstock_spring.exception.ResourceNotFoundException;
import tecstock_spring.exception.UsuarioEmUsoException;
import tecstock_spring.model.Usuario;
import tecstock_spring.model.Funcionario;
import tecstock_spring.model.Empresa;
import tecstock_spring.repository.UsuarioRepository;
import tecstock_spring.repository.FuncionarioRepository;
import tecstock_spring.repository.EmpresaRepository;
import tecstock_spring.repository.OrdemServicoRepository;
import tecstock_spring.repository.OrcamentoRepository;
import tecstock_spring.repository.ChecklistRepository;
import tecstock_spring.repository.CustomRevisionEntityRepository;
import tecstock_spring.util.TenantContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;
    private final FuncionarioRepository funcionarioRepository;
    private final EmpresaRepository empresaRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final ChecklistRepository checklistRepository;
    private final CustomRevisionEntityRepository customRevisionEntityRepository;
    private final PasswordEncoder passwordEncoder;
    Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    @Override
    @SuppressWarnings("null")
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
    @SuppressWarnings("null")
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
    @SuppressWarnings("null")
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
    @SuppressWarnings("null")
    public void deletar(Long id) {

        if (!TenantContext.isSuperAdmin()) {
            Long empresaId = TenantContext.getCurrentEmpresaId();
            if (empresaId == null) {
                throw new RuntimeException("Empresa não encontrada no contexto do usuário");
            }
        }

        Usuario usuario = buscarPorId(id);

        if (customRevisionEntityRepository.existsByUsuario(usuario.getNomeUsuario())) {
            throw new UsuarioEmUsoException("Usuário não pode ser excluído pois já registrou dados no sistema");
        }

        if (usuario.getNivelAcesso() == 2 && usuario.getConsultor() != null) {
            Long consultorId = usuario.getConsultor().getId();
            Long empresaId = TenantContext.getCurrentEmpresaId();

            if (ordemServicoRepository.existsByConsultorOrMecanicoInEmpresa(consultorId, empresaId)) {
                throw new UsuarioEmUsoException("Usuário não pode ser excluído pois o consultor vinculado está registrado em uma Ordem de Serviço");
            }

            if (orcamentoRepository.existsByConsultorOrMecanicoInEmpresa(consultorId, empresaId)) {
                throw new UsuarioEmUsoException("Usuário não pode ser excluído pois o consultor vinculado está registrado em um Orçamento");
            }

            if (checklistRepository.existsByConsultorIdAndEmpresaId(consultorId, empresaId)) {
                throw new UsuarioEmUsoException("Usuário não pode ser excluído pois o consultor vinculado está registrado em um Checklist");
            }
        }

        repository.deleteById(id);
    }

    @Override
    public Page<Usuario> buscarPaginado(String query, Pageable pageable) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Empresa não encontrada no contexto do usuário");
        }
        if (query == null || query.trim().isEmpty()) {
            return repository.findByEmpresaId(empresaId, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createdAt")));
        }
        return repository.searchByQueryAndEmpresaId(query.trim(), empresaId, pageable);
    }

    @Override
    public List<Usuario> listarUltimosParaInicio(int limit) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Empresa não encontrada no contexto do usuário");
        }
        Pageable pageable = PageRequest.of(0, limit);
        return repository.findTopUsuariosByEmpresaId(empresaId, pageable);
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
