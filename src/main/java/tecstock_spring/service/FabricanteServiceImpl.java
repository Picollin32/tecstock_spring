package tecstock_spring.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tecstock_spring.controller.FabricanteController;
import tecstock_spring.model.Fabricante;
import tecstock_spring.repository.FabricanteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FabricanteServiceImpl implements FabricanteService {

    private final FabricanteRepository repository;
    Logger logger = Logger.getLogger(FabricanteController.class);

    @Override
    public Fabricante salvar(Fabricante fabricante) {
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
        BeanUtils.copyProperties(novoFabricante, fabricanteExistente, "id");
        return repository.save(fabricanteExistente);
    }

    @Override
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
