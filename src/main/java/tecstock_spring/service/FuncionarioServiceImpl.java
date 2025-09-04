package tecstock_spring.service;

import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tecstock_spring.controller.FuncionarioController;
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
        Funcionario funcionariosalvo = repository.save(funcionario);
        if (funcionariosalvo != null) {
            logger.info("Funcionario salvo com sucesso: " + funcionariosalvo);
        } else {
            logger.error("Erro ao salvar funcionario: " + funcionario);
        }
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
        BeanUtils.copyProperties(novoFuncionario, funcionarioExistente, "id", "pecasComDesconto");
        return repository.save(funcionarioExistente);
    }

    @Override
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
