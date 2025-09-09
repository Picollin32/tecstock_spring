package tecstock_spring.service;

import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tecstock_spring.controller.FuncionarioController;
import tecstock_spring.exception.CpfDuplicadoException;
import tecstock_spring.model.Funcionario;
import tecstock_spring.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FuncionarioServiceImpl implements FuncionarioService {

    private final FuncionarioRepository repository;
    Logger logger = Logger.getLogger(FuncionarioController.class);

    @Override
    public Funcionario salvar(Funcionario funcionario) {
        validarCpfDuplicado(funcionario.getCpf(), null);
        Funcionario funcionariosalvo = repository.save(funcionario);
        logger.info("Funcionario salvo com sucesso: " + funcionariosalvo);
        return funcionariosalvo;
    }

    @Override
    public Funcionario buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionario não encontrado"));
    }

    @Override
    public List<Funcionario> listarTodos() {
        List<Funcionario> funcionarios = repository.findAll();
        if (funcionarios != null && funcionarios.isEmpty()) {
            logger.info("Nenhum funcionario cadastrado: " + funcionarios);
            System.out.println("Nenhum funcionario cadastrado: " + funcionarios);
        } else if (funcionarios != null && !funcionarios.isEmpty()) {
            logger.info(funcionarios.size() + " funcionarios encontrados.");
            System.out.println(funcionarios.size() + " funcionarios encontrados.");
        }
        return funcionarios;
    }

    @Override
    public Funcionario atualizar(Long id, Funcionario novoFuncionario) {
        Funcionario funcionarioExistente = buscarPorId(id);
        validarCpfDuplicado(novoFuncionario.getCpf(), id);
        BeanUtils.copyProperties(novoFuncionario, funcionarioExistente, "id", "pecasComDesconto", "createdAt", "updatedAt");
        return repository.save(funcionarioExistente);
    }

    @Override
    public void deletar(Long id) {
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
