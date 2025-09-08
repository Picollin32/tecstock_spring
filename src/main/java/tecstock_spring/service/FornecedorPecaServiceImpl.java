package tecstock_spring.service;

import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tecstock_spring.controller.FornecedorPecaController;
import tecstock_spring.model.FornecedorPeca;
import tecstock_spring.model.FornecedorPecaId;
import tecstock_spring.repository.FornecedorPecaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FornecedorPecaServiceImpl implements FornecedorPecaService {

    private final FornecedorPecaRepository repository;
    Logger logger = Logger.getLogger(FornecedorPecaController.class);

    @Override
    public FornecedorPeca salvar(FornecedorPeca fornecedorPeca) {
        FornecedorPeca fornecedorPecaSalvo = repository.save(fornecedorPeca);
        logger.info("Fornecedor de peças salvo com sucesso: " + fornecedorPecaSalvo);
        return fornecedorPecaSalvo;
    }

    @Override
    public FornecedorPeca buscarPorId(FornecedorPecaId id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fornecedor de peças não encontrado"));
    }

    @Override
    public List<FornecedorPeca> listarTodos() {
        List<FornecedorPeca> fornecedorPecas = repository.findAll();
        if (fornecedorPecas.isEmpty()) {
            logger.info("Nenhum fornecedor de peça cadastrado.");
        } else {
            logger.info(fornecedorPecas.size() + " Fornecedor de peças encontrados.");
        }
        return fornecedorPecas;
    }

    @Override
    public FornecedorPeca atualizar(FornecedorPecaId id, FornecedorPeca novoFornecedorPeca) {
        FornecedorPeca fornecedorPecaExistente = buscarPorId(id);
        BeanUtils.copyProperties(novoFornecedorPeca, fornecedorPecaExistente, "id", "createdAt", "updatedAt");
        return repository.save(fornecedorPecaExistente);
    }

    @Override
    public void deletar(FornecedorPecaId id) {
        repository.deleteById(id);
    }
}