package tecstock_spring.service;

import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tecstock_spring.controller.VeiculoController;
import tecstock_spring.exception.PlacaDuplicadaException;
import tecstock_spring.exception.VeiculoEmUsoException;
import tecstock_spring.model.Veiculo;
import tecstock_spring.repository.VeiculoRepository;
import tecstock_spring.repository.ChecklistRepository;
import tecstock_spring.repository.AgendamentoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VeiculoServiceImpl implements VeiculoService {

    private final VeiculoRepository repository;
    private final ChecklistRepository checklistRepository;
    private final AgendamentoRepository agendamentoRepository;
    Logger logger = Logger.getLogger(VeiculoController.class);

    @Override
    public Veiculo salvar(Veiculo veiculo) {
        if (repository.existsByPlaca(veiculo.getPlaca())) {
            throw new PlacaDuplicadaException("Placa já cadastrada");
        }
        
        Veiculo veiculoSalvo = repository.save(veiculo);
        logger.info("Veículo salvo com sucesso: " + veiculoSalvo);
        return veiculoSalvo;
    }

    @Override
    public Veiculo buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
    }

    @Override
    public List<Veiculo> listarTodos() {
        List<Veiculo> veiculos = repository.findAll();
        if (veiculos.isEmpty()) {
            logger.info("Nenhum veículo cadastrado.");
        } else {
            logger.info(veiculos.size() + " veículos encontrados.");
        }
        return veiculos;
    }

    @Override
    public Veiculo atualizar(Long id, Veiculo novoVeiculo) {
        Veiculo veiculoExistente = buscarPorId(id);

        if (repository.existsByPlacaAndIdNot(novoVeiculo.getPlaca(), id)) {
            throw new PlacaDuplicadaException("Placa já cadastrada");
        }

        BeanUtils.copyProperties(novoVeiculo, veiculoExistente, "id", "createdAt", "updatedAt");
        return repository.save(veiculoExistente);
    }

    @Override
    public void deletar(Long id) {
        Veiculo veiculo = buscarPorId(id);
        
        if (checklistRepository.existsByVeiculoPlaca(veiculo.getPlaca())) {
            throw new VeiculoEmUsoException("Veículo não pode ser excluído pois está vinculado a um checklist");
        }
        
        if (agendamentoRepository.existsByPlacaVeiculo(veiculo.getPlaca())) {
            throw new VeiculoEmUsoException("Veículo não pode ser excluído pois está vinculado a um agendamento");
        }
        
        repository.deleteById(id);
        logger.info("Veículo excluído com sucesso: " + veiculo.getPlaca());
    }
}
