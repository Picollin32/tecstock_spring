package tecstock_spring.service;

import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tecstock_spring.controller.MarcaController;
import tecstock_spring.exception.NomeDuplicadoException;
import tecstock_spring.model.Marca;
import tecstock_spring.repository.MarcaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarcaServiceImpl implements MarcaService {

    private final MarcaRepository repository;
    Logger logger = Logger.getLogger(MarcaController.class);

    @Override
    public Marca salvar(Marca marca) {
        validarNomeDuplicado(marca.getMarca(), null);
        Marca marcaSalva = repository.save(marca);
        logger.info("Marca salva com sucesso: " + marcaSalva);
        return marcaSalva;
    }

    @Override
    public Marca buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Marca não encontrado"));
    }

    @Override
    public List<Marca> listarTodos() {
        List<Marca> marcas = repository.findAll();
        if (marcas != null && marcas.isEmpty()) {
            logger.info("Nenhuma marca cadastrada: " + marcas);
            System.out.println("Nenhuma marca cadastrada: " + marcas);
        } else if (marcas != null && !marcas.isEmpty()) {
            logger.info(marcas.size() + " marcas encontrados.");
            System.out.println(marcas.size() + " marcas encontrados.");
        }
        return marcas;
    }

    @Override
    public Marca atualizar(Long id, Marca novoMarca) {
        Marca categoriaExistente = buscarPorId(id);
        validarNomeDuplicado(novoMarca.getMarca(), id);
        BeanUtils.copyProperties(novoMarca, categoriaExistente, "id", "createdAt", "updatedAt");
        return repository.save(categoriaExistente);
    }

    @Override
    public void deletar(Long id) {
        repository.deleteById(id);
    }
    
    private void validarNomeDuplicado(String nome, Long idExcluir) {
        logger.info("Validando nome duplicado para marca: " + nome + " (excluindo ID: " + idExcluir + ")");
        
        if (nome == null || nome.trim().isEmpty()) {
            logger.warn("Nome da marca é nulo ou vazio");
            return;
        }
        
        String nomeLimpo = nome.trim();
        logger.info("Nome limpo para validação: " + nomeLimpo);
        
        boolean exists;
        if (idExcluir != null) {
            exists = repository.existsByNomeIgnoreCaseAndIdNot(nomeLimpo, idExcluir);
            logger.info("Verificação para atualização - Existe outra marca com nome " + nomeLimpo + " (excluindo ID " + idExcluir + "): " + exists);
        } else {
            exists = repository.existsByNomeIgnoreCase(nomeLimpo);
            logger.info("Verificação para criação - Existe marca com nome " + nomeLimpo + ": " + exists);
        }
        
        if (exists) {
            String mensagem = "Nome da marca já está cadastrado";
            logger.error(mensagem + ": " + nomeLimpo);
            throw new NomeDuplicadoException(mensagem);
        }
        
        logger.info("Validação de nome concluída com sucesso para marca: " + nomeLimpo);
    }
}
