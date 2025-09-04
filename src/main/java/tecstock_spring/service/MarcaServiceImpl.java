package tecstock_spring.service;

import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tecstock_spring.controller.MarcaController;
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
        Marca marcaSalva = repository.save(marca);
        if (marcaSalva != null) {
            logger.info("Marca salva com sucesso: " + marcaSalva);
        } else {
            logger.error("Erro ao salvar marca: " + marca);
        }
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
        BeanUtils.copyProperties(novoMarca, categoriaExistente, "id");
        return repository.save(categoriaExistente);
    }

    @Override
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
