package tecstock_spring.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import tecstock_spring.controller.PecaController;
import tecstock_spring.model.Fabricante;
import tecstock_spring.model.Peca;
import tecstock_spring.repository.FabricanteRepository;
import tecstock_spring.repository.PecaRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PecaServiceImpl implements PecaService {

    private final PecaRepository pecaRepository;
    private final FabricanteRepository fabricanteRepository; 
    
    Logger logger = Logger.getLogger(PecaController.class);

    @Override
    public Peca salvar(Peca peca) {
        if (peca.getFabricante() == null || peca.getFabricante().getId() == null) {
            throw new IllegalArgumentException("O ID do Fabricante não pode ser nulo ao salvar uma Peça.");
        }

        Fabricante fabricanteGerenciado = fabricanteRepository.findById(peca.getFabricante().getId())
                .orElseThrow(() -> new RuntimeException("Fabricante com ID " + peca.getFabricante().getId() + " não encontrado."));

        peca.setFabricante(fabricanteGerenciado);

        Peca pecaSalva = pecaRepository.save(peca);
        
        logger.info("Peça salva com sucesso: " + pecaSalva);
        return pecaSalva;
    }

    @Override
    public Peca buscarPorId(Long id) {
        return pecaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Peça não encontrada"));
    }

    @Override
    public List<Peca> listarTodas() {
        List<Peca> pecas = pecaRepository.findAll();
        if (pecas.isEmpty()) {
            logger.info("Nenhuma peça cadastrada.");
        } else {
            logger.info(pecas.size() + " peças encontradas.");
        }
        return pecas;
    }

    @Override
    public Peca atualizar(Long id, Peca novaPeca) {
        Peca pecaExistente = buscarPorId(id);
        BeanUtils.copyProperties(novaPeca, pecaExistente, "id");
        return pecaRepository.save(pecaExistente);
    }

    @Override
    public void deletar(Long id) {
        pecaRepository.deleteById(id);
    }
}