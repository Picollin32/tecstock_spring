package tecstock_spring.service;

import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tecstock_spring.controller.FornecedorController;
import tecstock_spring.exception.FornecedorEmUsoException;
import tecstock_spring.model.Fornecedor;
import tecstock_spring.repository.FornecedorRepository;
import tecstock_spring.repository.PecaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FornecedorServiceImpl implements FornecedorService {

    private final FornecedorRepository repository;
    private final PecaRepository pecaRepository;
    Logger logger = Logger.getLogger(FornecedorController.class);

    @Override
    public Fornecedor salvar(Fornecedor fornecedor) {
        if (repository.existsByCnpj(fornecedor.getCnpj())) {
            throw new RuntimeException("CNPJ já cadastrado: " + fornecedor.getCnpj());
        }
        
        Fornecedor fornecedorSalvo = repository.save(fornecedor);
        logger.info("Fornecedor salvo com sucesso: " + fornecedorSalvo);
        return fornecedorSalvo;
    }

    @Override
    public Fornecedor buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
    }

    @Override
    public List<Fornecedor> listarTodos() {
        List<Fornecedor> fornecedores = repository.findAll();
        if (fornecedores != null && fornecedores.isEmpty()) {
            logger.info("Nenhum fornecedor cadastrado: " + fornecedores);
        } else if (fornecedores != null && !fornecedores.isEmpty()) {
            logger.info(fornecedores.size() + " fornecedores encontrados.");
        }
        return fornecedores;
    }

    @Override
    public Fornecedor atualizar(Long id, Fornecedor novoFornecedor) {
        Fornecedor fornecedorExistente = buscarPorId(id);
        
        if (repository.existsByCnpjAndIdNot(novoFornecedor.getCnpj(), id)) {
            throw new RuntimeException("CNPJ já cadastrado em outro fornecedor: " + novoFornecedor.getCnpj());
        }
        
        BeanUtils.copyProperties(novoFornecedor, fornecedorExistente, "id", "pecasComDesconto", "createdAt", "updatedAt");
        return repository.save(fornecedorExistente);
    }

    @Override
    public void deletar(Long id) {
        Fornecedor fornecedor = buscarPorId(id);
            if (pecaRepository.existsByFornecedor(fornecedor)) {
            throw new FornecedorEmUsoException("Fornecedor não pode ser excluído pois está vinculado a uma peça");
        }
        
        repository.deleteById(id);
        logger.info("Fornecedor excluído com sucesso: " + fornecedor.getNome());
    }
}
