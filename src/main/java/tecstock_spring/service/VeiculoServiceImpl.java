package tecstock_spring.service;

import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tecstock_spring.controller.VeiculoController;
import tecstock_spring.exception.PlacaDuplicadaException;
import tecstock_spring.exception.VeiculoEmUsoException;
import tecstock_spring.model.Empresa;
import tecstock_spring.model.Veiculo;
import tecstock_spring.repository.EmpresaRepository;
import tecstock_spring.repository.VeiculoRepository;
import tecstock_spring.util.TenantContext;
import tecstock_spring.repository.ChecklistRepository;
import tecstock_spring.repository.AgendamentoRepository;
import tecstock_spring.repository.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VeiculoServiceImpl implements VeiculoService {

    private final VeiculoRepository repository;
    private final EmpresaRepository empresaRepository;
    private final ChecklistRepository checklistRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    Logger logger = Logger.getLogger(VeiculoController.class);

    @Override
    public Veiculo salvar(Veiculo veiculo) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        if (repository.existsByPlacaAndEmpresaId(veiculo.getPlaca(), empresaId)) {
            throw new PlacaDuplicadaException("Placa já cadastrada nesta empresa");
        }
        
        Empresa empresa = empresaRepository.findById(empresaId)
            .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        veiculo.setEmpresa(empresa);
        
        Veiculo veiculoSalvo = repository.save(veiculo);
        logger.info("Veículo salvo com sucesso na empresa " + empresaId + ": " + veiculoSalvo);
        return veiculoSalvo;
    }

    @Override
    public Veiculo buscarPorId(Long id) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        return repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado ou não pertence à sua empresa"));
    }

    @Override
    public List<Veiculo> listarTodos() {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        List<Veiculo> veiculos = repository.findByEmpresaId(empresaId);
        if (veiculos.isEmpty()) {
            logger.info("Nenhum veículo cadastrado na empresa " + empresaId);
        } else {
            logger.info(veiculos.size() + " veículos encontrados na empresa " + empresaId);
        }
        return veiculos;
    }

    @Override
    public Veiculo atualizar(Long id, Veiculo novoVeiculo) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        Veiculo veiculoExistente = repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado ou não pertence à sua empresa"));

        if (repository.existsByPlacaAndIdNotAndEmpresaId(novoVeiculo.getPlaca(), id, empresaId)) {
            throw new PlacaDuplicadaException("Placa já cadastrada nesta empresa");
        }

        BeanUtils.copyProperties(novoVeiculo, veiculoExistente, "id", "empresa", "createdAt", "updatedAt");
        return repository.save(veiculoExistente);
    }

    @Override
    public void deletar(Long id) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        Veiculo veiculo = repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado ou não pertence à sua empresa"));
        
        if (ordemServicoRepository != null && ordemServicoRepository.existsByVeiculoPlaca(veiculo.getPlaca())) {
            throw new VeiculoEmUsoException("Veículo não pode ser excluído: está em uso em uma Ordem de Serviço (OS) com placa " + veiculo.getPlaca());
        }

        if (checklistRepository.existsByVeiculoPlaca(veiculo.getPlaca())) {
            throw new VeiculoEmUsoException("Veículo não pode ser excluído: está em uso em um Checklist com placa " + veiculo.getPlaca());
        }

        if (agendamentoRepository.existsByPlacaVeiculo(veiculo.getPlaca())) {
            throw new VeiculoEmUsoException("Veículo não pode ser excluído: está em uso em um Agendamento com placa " + veiculo.getPlaca());
        }
        
        repository.deleteById(id);
        logger.info("Veículo excluído com sucesso da empresa " + empresaId + ": " + veiculo.getPlaca());
    }
}
