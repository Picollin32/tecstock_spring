package tecstock_spring.service;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import tecstock_spring.exception.OrdemServicoNotFoundException;
import tecstock_spring.model.OrdemServico;
import tecstock_spring.repository.OrdemServicoRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdemServicoServiceImpl implements OrdemServicoService {

    private final OrdemServicoRepository repository;
    private static final Logger logger = Logger.getLogger(OrdemServicoServiceImpl.class);

    @Override
    public OrdemServico salvar(OrdemServico ordemServico) {
        if (ordemServico.getId() == null && (ordemServico.getNumeroOS() == null || ordemServico.getNumeroOS().isEmpty())) {
            Integer max = repository.findAll().stream()
                .filter(os -> os.getNumeroOS() != null && os.getNumeroOS().matches("\\d+"))
                .mapToInt(os -> Integer.parseInt(os.getNumeroOS()))
                .max()
                .orElse(0);
            ordemServico.setNumeroOS(String.valueOf(max + 1));
            logger.info("Gerando novo número de OS: " + ordemServico.getNumeroOS());
        }
        
        ordemServico.calcularTodosOsPrecos();
        logger.info("Preços calculados - Serviços: R$ " + ordemServico.getPrecoTotalServicos() + 
                   ", Peças: R$ " + ordemServico.getPrecoTotalPecas() + 
                   ", Total: R$ " + ordemServico.getPrecoTotal());
        
        OrdemServico ordemServicoSalva = repository.save(ordemServico);
        logger.info("Ordem de Serviço salva com sucesso: " + ordemServicoSalva.getNumeroOS());
        return ordemServicoSalva;
    }

    @Override
    public OrdemServico buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new OrdemServicoNotFoundException("Ordem de Serviço não encontrada com ID: " + id));
    }
    
    @Override
    public OrdemServico buscarPorNumeroOS(String numeroOS) {
        return repository.findByNumeroOS(numeroOS)
                .orElseThrow(() -> new OrdemServicoNotFoundException("Ordem de Serviço não encontrada com número: " + numeroOS));
    }

    @Override
    public List<OrdemServico> listarTodos() {
        List<OrdemServico> ordensServico = repository.findAllByOrderByCreatedAtDesc();
        logger.info(ordensServico.size() + " ordens de serviço encontradas.");
        return ordensServico;
    }
    
    @Override
    public List<OrdemServico> listarPorCliente(String clienteCpf) {
        return repository.findByClienteCpfOrderByDataHoraDesc(clienteCpf);
    }
    
    @Override
    public List<OrdemServico> listarPorVeiculo(String veiculoPlaca) {
        return repository.findByVeiculoPlacaOrderByDataHoraDesc(veiculoPlaca);
    }
    
    @Override
    public List<OrdemServico> listarPorStatus(String status) {
        return repository.findByStatusOrderByDataHoraDesc(status);
    }
    
    @Override
    public List<OrdemServico> listarPorChecklist(Long checklistId) {
        return repository.findByChecklistIdOrderByDataHoraDesc(checklistId);
    }
    
    @Override
    public List<OrdemServico> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return repository.findByDataHoraBetween(inicio, fim);
    }

    @Override
    public OrdemServico atualizar(Long id, OrdemServico novaOrdemServico) {
        OrdemServico ordemServicoExistente = buscarPorId(id);
        
        String numeroOSOriginal = ordemServicoExistente.getNumeroOS();
        LocalDateTime createdAtOriginal = ordemServicoExistente.getCreatedAt();
        ordemServicoExistente.setDataHora(novaOrdemServico.getDataHora());
        ordemServicoExistente.setClienteNome(novaOrdemServico.getClienteNome());
        ordemServicoExistente.setClienteCpf(novaOrdemServico.getClienteCpf());
        ordemServicoExistente.setClienteTelefone(novaOrdemServico.getClienteTelefone());
        ordemServicoExistente.setClienteEmail(novaOrdemServico.getClienteEmail());
        ordemServicoExistente.setVeiculoNome(novaOrdemServico.getVeiculoNome());
        ordemServicoExistente.setVeiculoMarca(novaOrdemServico.getVeiculoMarca());
        ordemServicoExistente.setVeiculoAno(novaOrdemServico.getVeiculoAno());
        ordemServicoExistente.setVeiculoCor(novaOrdemServico.getVeiculoCor());
        ordemServicoExistente.setVeiculoPlaca(novaOrdemServico.getVeiculoPlaca());
        ordemServicoExistente.setVeiculoQuilometragem(novaOrdemServico.getVeiculoQuilometragem());
        ordemServicoExistente.setVeiculoCategoria(novaOrdemServico.getVeiculoCategoria());
        ordemServicoExistente.setChecklistId(novaOrdemServico.getChecklistId());
        ordemServicoExistente.setQueixaPrincipal(novaOrdemServico.getQueixaPrincipal());
        ordemServicoExistente.setGarantiaMeses(novaOrdemServico.getGarantiaMeses());
        ordemServicoExistente.setTipoPagamento(novaOrdemServico.getTipoPagamento());
        ordemServicoExistente.setNumeroParcelas(novaOrdemServico.getNumeroParcelas());
        ordemServicoExistente.setNomeMecanico(novaOrdemServico.getNomeMecanico());
        ordemServicoExistente.setNomeConsultor(novaOrdemServico.getNomeConsultor());
        ordemServicoExistente.setObservacoes(novaOrdemServico.getObservacoes());
        ordemServicoExistente.setStatus(novaOrdemServico.getStatus());
        ordemServicoExistente.getServicosRealizados().clear();
        if (novaOrdemServico.getServicosRealizados() != null) {
            ordemServicoExistente.getServicosRealizados().addAll(novaOrdemServico.getServicosRealizados());
        }
        
        ordemServicoExistente.getPecasUtilizadas().clear();
        if (novaOrdemServico.getPecasUtilizadas() != null) {
            for (tecstock_spring.model.PecaOrdemServico peca : novaOrdemServico.getPecasUtilizadas()) {
                ordemServicoExistente.getPecasUtilizadas().add(peca);
            }
        }

        ordemServicoExistente.setNumeroOS(numeroOSOriginal);
        ordemServicoExistente.setCreatedAt(createdAtOriginal);

        ordemServicoExistente.calcularTodosOsPrecos();
        logger.info("Preços recalculados na atualização - Serviços: R$ " + ordemServicoExistente.getPrecoTotalServicos() + 
                   ", Peças: R$ " + ordemServicoExistente.getPrecoTotalPecas() + 
                   ", Total: R$ " + ordemServicoExistente.getPrecoTotal());
        
        logger.info("Atualizando Ordem de Serviço ID: " + id + " - Preservando número: " + numeroOSOriginal);
        return repository.save(ordemServicoExistente);
    }

    @Override
    public void deletar(Long id) {
        OrdemServico ordemServico = buscarPorId(id);
        logger.info("Deletando Ordem de Serviço: " + ordemServico.getNumeroOS());
        repository.deleteById(id);
    }
}
