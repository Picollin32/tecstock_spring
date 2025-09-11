package tecstock_spring.service;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
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
        
        if (ordemServico.getServicosRealizados() != null && !ordemServico.getServicosRealizados().isEmpty()) {
            String categoriaVeiculo = ordemServico.getVeiculoCategoria();
            double total = ordemServico.getServicosRealizados().stream()
                .mapToDouble(servico -> {
                    return servico.precoParaCategoria(categoriaVeiculo);
                })
                .sum();
            ordemServico.setPrecoTotal(total);
            logger.info("Preço total calculado para categoria " + categoriaVeiculo + ": R$ " + total);
        }
        
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
        
        BeanUtils.copyProperties(novaOrdemServico, ordemServicoExistente, "id", "numeroOS", "createdAt", "updatedAt");

        ordemServicoExistente.setNumeroOS(numeroOSOriginal);
        ordemServicoExistente.setCreatedAt(createdAtOriginal);
        

        if (ordemServicoExistente.getServicosRealizados() != null && !ordemServicoExistente.getServicosRealizados().isEmpty()) {
            String categoriaVeiculo = ordemServicoExistente.getVeiculoCategoria();
            double total = ordemServicoExistente.getServicosRealizados().stream()
                .mapToDouble(servico -> {
                    return servico.precoParaCategoria(categoriaVeiculo);
                })
                .sum();
            ordemServicoExistente.setPrecoTotal(total);
        }
        
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
