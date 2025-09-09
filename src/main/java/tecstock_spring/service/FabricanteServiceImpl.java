package tecstock_spring.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tecstock_spring.controller.FabricanteController;
import tecstock_spring.exception.NomeDuplicadoException;
import tecstock_spring.exception.FabricanteEmUsoException;
import tecstock_spring.model.Fabricante;
import tecstock_spring.repository.FabricanteRepository;
import tecstock_spring.repository.PecaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FabricanteServiceImpl implements FabricanteService {

    private final FabricanteRepository repository;
    private final PecaRepository pecaRepository;
    Logger logger = Logger.getLogger(FabricanteController.class);

    @Override
    public Fabricante salvar(Fabricante fabricante) {
        validarNomeDuplicado(fabricante.getNome(), null);
        Fabricante fabricanteSalvo = repository.save(fabricante);
        logger.info("Fabricante salvo com sucesso: " + fabricanteSalvo);
        return fabricanteSalvo;
    }

    @Override
    public Fabricante buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fabricante não encontrado"));
    }

    @Override
    public List<Fabricante> listarTodos() {
        List<Fabricante> fabricantes = repository.findAll();
        if (fabricantes.isEmpty()) {
            logger.info("Nenhum fabricante cadastrado.");
        } else {
            logger.info(fabricantes.size() + " fabricantes encontrados.");
        }
        return fabricantes;
    }

    @Override
    public Fabricante atualizar(Long id, Fabricante novoFabricante) {
        Fabricante fabricanteExistente = buscarPorId(id);
        validarNomeDuplicado(novoFabricante.getNome(), id);
        BeanUtils.copyProperties(novoFabricante, fabricanteExistente, "id", "createdAt", "updatedAt");
        return repository.save(fabricanteExistente);
    }

    @Override
    public void deletar(Long id) {
        Fabricante fabricante = buscarPorId(id);
        
        if (pecaRepository.existsByFabricante(fabricante)) {
            throw new FabricanteEmUsoException("Fabricante não pode ser excluído pois está vinculado a uma peça");
        }
        
        repository.deleteById(id);
        logger.info("Fabricante excluído com sucesso: " + fabricante.getNome());
    }
    
    private void validarNomeDuplicado(String nome, Long idExcluir) {
        logger.info("Validando nome duplicado para fabricante: " + nome + " (excluindo ID: " + idExcluir + ")");
        
        if (nome == null || nome.trim().isEmpty()) {
            logger.warn("Nome do fabricante é nulo ou vazio");
            return;
        }
        
        String nomeLimpo = nome.trim();
        logger.info("Nome limpo para validação: " + nomeLimpo);
        
        boolean exists;
        if (idExcluir != null) {
            exists = repository.existsByNomeIgnoreCaseAndIdNot(nomeLimpo, idExcluir);
            logger.info("Verificação para atualização - Existe outro fabricante com nome " + nomeLimpo + " (excluindo ID " + idExcluir + "): " + exists);
        } else {
            exists = repository.existsByNomeIgnoreCase(nomeLimpo);
            logger.info("Verificação para criação - Existe fabricante com nome " + nomeLimpo + ": " + exists);
        }
        
        if (exists) {
            String mensagem = "Nome do fabricante já está cadastrado";
            logger.error(mensagem + ": " + nomeLimpo);
            throw new NomeDuplicadoException(mensagem);
        }
        
        logger.info("Validação de nome concluída com sucesso para fabricante: " + nomeLimpo);
    }
}
