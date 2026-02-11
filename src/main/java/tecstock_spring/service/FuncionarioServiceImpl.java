package tecstock_spring.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tecstock_spring.controller.FuncionarioController;
import tecstock_spring.exception.CpfDuplicadoException;
import tecstock_spring.exception.ResourceNotFoundException;
import tecstock_spring.model.Funcionario;
import tecstock_spring.model.Empresa;
import tecstock_spring.repository.FuncionarioRepository;
import tecstock_spring.repository.OrdemServicoRepository;
import tecstock_spring.repository.OrcamentoRepository;
import tecstock_spring.repository.ChecklistRepository;
import tecstock_spring.repository.EmpresaRepository;
import tecstock_spring.repository.UsuarioRepository;
import tecstock_spring.exception.FuncionarioEmUsoException;
import tecstock_spring.util.TenantContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FuncionarioServiceImpl implements FuncionarioService {

    private final FuncionarioRepository repository;
    private final EmpresaRepository empresaRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final ChecklistRepository checklistRepository;
    private final UsuarioRepository usuarioRepository;
    Logger logger = LoggerFactory.getLogger(FuncionarioController.class);

    @Override
    public Funcionario salvar(Funcionario funcionario) {

        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Empresa não encontrada no contexto do usuário");
        }
        
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
        
        funcionario.setEmpresa(empresa);
        
        validarCpfDuplicado(funcionario.getCpf(), null);
        Funcionario funcionariosalvo = repository.save(funcionario);
        logger.info("Funcionario salvo com sucesso: " + funcionariosalvo);
        return funcionariosalvo;
    }

    @Override
    public Funcionario buscarPorId(Long id) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Empresa não encontrada no contexto do usuário");
        }
        
        return repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario não encontrado"));
    }

    @Override
    public List<Funcionario> listarTodos() {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Empresa não encontrada no contexto do usuário");
        }
        
        List<Funcionario> funcionarios = repository.findByEmpresaId(empresaId);
        if (funcionarios != null && funcionarios.isEmpty()) {
            logger.info("Nenhum funcionario cadastrado na empresa " + empresaId);
            System.out.println("Nenhum funcionario cadastrado na empresa " + empresaId);
        } else if (funcionarios != null && !funcionarios.isEmpty()) {
            logger.info(funcionarios.size() + " funcionarios encontrados na empresa " + empresaId);
            System.out.println(funcionarios.size() + " funcionarios encontrados na empresa " + empresaId);
        }
        return funcionarios;
    }

    @Override
    public List<Funcionario> listarMecanicos() {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Empresa não encontrada no contexto do usuário");
        }
        
        List<Funcionario> mecanicos = repository.findByEmpresaId(empresaId).stream()
                .filter(f -> f.getNivelAcesso() == 3)
                .toList();
        logger.info(mecanicos.size() + " mecânicos encontrados na empresa " + empresaId);
        return mecanicos;
    }

    @Override
    @SuppressWarnings("null")
    public Funcionario atualizar(Long id, Funcionario novoFuncionario) {

        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Empresa não encontrada no contexto do usuário");
        }
        
        Funcionario funcionarioExistente = buscarPorId(id);
        validarCpfDuplicado(novoFuncionario.getCpf(), id);
        BeanUtils.copyProperties(novoFuncionario, funcionarioExistente, "id", "pecasComDesconto", "createdAt", "updatedAt", "empresa");
        return repository.save(funcionarioExistente);
    }

    @Override
    @SuppressWarnings("null")
    public void deletar(Long id) {

        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Empresa não encontrada no contexto do usuário");
        }
        
        boolean emOrdemComoMecanico = ordemServicoRepository.existsByMecanicoIdAndEmpresaId(id, empresaId);
        boolean emOrdemComoConsultor = ordemServicoRepository.existsByConsultorIdAndEmpresaId(id, empresaId);
        boolean emOrcamentoComoMecanico = orcamentoRepository.existsByMecanicoIdAndEmpresaId(id, empresaId);
        boolean emOrcamentoComoConsultor = orcamentoRepository.existsByConsultorIdAndEmpresaId(id, empresaId);
        boolean emChecklistComoConsultor = checklistRepository.existsByConsultorIdAndEmpresaId(id, empresaId);
        boolean emUsuarioComoConsultor = usuarioRepository.existsByConsultorIdAndEmpresaId(id, empresaId);

        if (emOrdemComoMecanico) {
            throw new FuncionarioEmUsoException("Funcionário não pode ser excluído pois está vinculado como mecânico em uma Ordem de Serviço");
        }

        if (emOrdemComoConsultor) {
            throw new FuncionarioEmUsoException("Funcionário não pode ser excluído pois está vinculado como consultor em uma Ordem de Serviço");
        }

        if (emChecklistComoConsultor) {
            throw new FuncionarioEmUsoException("Funcionário não pode ser excluído pois está vinculado como consultor em um Checklist");
        }

        if (emOrcamentoComoMecanico) {
            throw new FuncionarioEmUsoException("Funcionário não pode ser excluído pois está vinculado como mecânico em um Orçamento");
        }

        if (emOrcamentoComoConsultor) {
            throw new FuncionarioEmUsoException("Funcionário não pode ser excluído pois está vinculado como consultor em um Orçamento");
        }

        if (emUsuarioComoConsultor) {
            throw new FuncionarioEmUsoException("Funcionário não pode ser excluído pois está vinculado como consultor em um Usuário");
        }

        repository.deleteById(id);
    }
    
    private void validarCpfDuplicado(String cpf, Long idExcluir) {
        logger.info("Validando CPF duplicado: " + cpf + " (excluindo ID: " + idExcluir + ")");
        
        if (cpf == null || cpf.trim().isEmpty()) {
            logger.warn("CPF é nulo ou vazio");
            return;
        }
        
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        logger.info("CPF limpo para validação: " + cpfLimpo);
        
        boolean exists;
        if (idExcluir != null) {
            exists = repository.existsByCpfAndIdNot(cpfLimpo, idExcluir);
            logger.info("Verificação para atualização - Existe outro funcionário com CPF " + cpfLimpo + " (excluindo ID " + idExcluir + "): " + exists);
        } else {
            exists = repository.existsByCpf(cpfLimpo);
            logger.info("Verificação para criação - Existe funcionário com CPF " + cpfLimpo + ": " + exists);
        }
        
        if (exists) {
            String mensagem = "CPF já cadastrado";
            logger.error(mensagem + ": " + cpfLimpo);
            throw new CpfDuplicadoException(mensagem);
        }
        
        logger.info("Validação de CPF concluída com sucesso para: " + cpfLimpo);
    }
}
