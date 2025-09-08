package tecstock_spring.service;

import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tecstock_spring.controller.VeiculoController;
import tecstock_spring.model.Veiculo;
import tecstock_spring.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VeiculoServiceImpl implements VeiculoService {

    private final VeiculoRepository repository;
    Logger logger = Logger.getLogger(VeiculoController.class);

    @Override
    public Veiculo salvar(Veiculo veiculo) {
        // Validar se placa já existe
        if (repository.existsByPlaca(veiculo.getPlaca())) {
            throw new RuntimeException("Placa já cadastrada: " + veiculo.getPlaca());
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
        
        // Validar se placa já existe em outro veículo
        if (repository.existsByPlacaAndIdNot(novoVeiculo.getPlaca(), id)) {
            throw new RuntimeException("Placa já cadastrada em outro veículo: " + novoVeiculo.getPlaca());
        }
        
        // Preservar o createdAt original e não copiar updatedAt para manter a lógica de auditoria
        BeanUtils.copyProperties(novoVeiculo, veiculoExistente, "id", "createdAt", "updatedAt");
        return repository.save(veiculoExistente);
    }

    @Override
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
