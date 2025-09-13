package tecstock_spring.service;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import tecstock_spring.exception.OrcamentoNotFoundException;
import tecstock_spring.model.Orcamento;
import tecstock_spring.repository.OrcamentoRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrcamentoServiceImpl implements OrcamentoService {

    private final OrcamentoRepository repository;
    private static final Logger logger = Logger.getLogger(OrcamentoServiceImpl.class);

    @Override
    public Orcamento salvar(Orcamento orcamento) {
        if (orcamento.getId() == null && (orcamento.getNumeroOrcamento() == null || orcamento.getNumeroOrcamento().isEmpty())) {
            Integer max = repository.findAll().stream()
                .filter(orc -> orc.getNumeroOrcamento() != null && orc.getNumeroOrcamento().matches("\\d+"))
                .mapToInt(orc -> Integer.parseInt(orc.getNumeroOrcamento()))
                .max()
                .orElse(0);
            orcamento.setNumeroOrcamento(String.valueOf(max + 1));
            logger.info("Gerando novo número de orçamento: " + orcamento.getNumeroOrcamento());
        }
        
        orcamento.calcularTodosOsPrecos();
        logger.info("Preços calculados - Serviços: R$ " + orcamento.getPrecoTotalServicos() + 
                   ", Peças: R$ " + orcamento.getPrecoTotalPecas() + 
                   ", Total: R$ " + orcamento.getPrecoTotal());
        
        Orcamento orcamentoSalvo = repository.save(orcamento);
        logger.info("Orçamento salvo com sucesso: " + orcamentoSalvo.getNumeroOrcamento());
        return orcamentoSalvo;
    }

    @Override
    public Orcamento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new OrcamentoNotFoundException("Orçamento não encontrado com ID: " + id));
    }
    
    @Override
    public Orcamento buscarPorNumeroOrcamento(String numeroOrcamento) {
        return repository.findByNumeroOrcamento(numeroOrcamento)
                .orElseThrow(() -> new OrcamentoNotFoundException("Orçamento não encontrado com número: " + numeroOrcamento));
    }

    @Override
    public List<Orcamento> listarTodos() {
        List<Orcamento> orcamentos = repository.findAllByOrderByCreatedAtDesc();
        logger.info(orcamentos.size() + " orçamentos encontrados.");
        return orcamentos;
    }
    
    @Override
    public List<Orcamento> listarPorCliente(String clienteCpf) {
        return repository.findByClienteCpfOrderByDataHoraDesc(clienteCpf);
    }
    
    @Override
    public List<Orcamento> listarPorVeiculo(String veiculoPlaca) {
        return repository.findByVeiculoPlacaOrderByDataHoraDesc(veiculoPlaca);
    }
    
    @Override
    public List<Orcamento> listarPorStatus(String status) {
        return repository.findByStatusOrderByDataHoraDesc(status);
    }
    
    @Override
    public List<Orcamento> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return repository.findByDataHoraBetween(inicio, fim);
    }

    @Override
    public Orcamento atualizar(Long id, Orcamento novoOrcamento) {
        Orcamento orcamentoExistente = buscarPorId(id);
        
        String numeroOrcamentoOriginal = orcamentoExistente.getNumeroOrcamento();
        LocalDateTime createdAtOriginal = orcamentoExistente.getCreatedAt();
        orcamentoExistente.setDataHora(novoOrcamento.getDataHora());
        orcamentoExistente.setClienteNome(novoOrcamento.getClienteNome());
        orcamentoExistente.setClienteCpf(novoOrcamento.getClienteCpf());
        orcamentoExistente.setClienteTelefone(novoOrcamento.getClienteTelefone());
        orcamentoExistente.setClienteEmail(novoOrcamento.getClienteEmail());
        orcamentoExistente.setVeiculoNome(novoOrcamento.getVeiculoNome());
        orcamentoExistente.setVeiculoMarca(novoOrcamento.getVeiculoMarca());
        orcamentoExistente.setVeiculoAno(novoOrcamento.getVeiculoAno());
        orcamentoExistente.setVeiculoCor(novoOrcamento.getVeiculoCor());
        orcamentoExistente.setVeiculoPlaca(novoOrcamento.getVeiculoPlaca());
        orcamentoExistente.setVeiculoQuilometragem(novoOrcamento.getVeiculoQuilometragem());
        orcamentoExistente.setVeiculoCategoria(novoOrcamento.getVeiculoCategoria());
        orcamentoExistente.setQueixaPrincipal(novoOrcamento.getQueixaPrincipal());
        orcamentoExistente.setGarantiaMeses(novoOrcamento.getGarantiaMeses());
        orcamentoExistente.setTipoPagamento(novoOrcamento.getTipoPagamento());
        orcamentoExistente.setNumeroParcelas(novoOrcamento.getNumeroParcelas());
        orcamentoExistente.setNomeMecanico(novoOrcamento.getNomeMecanico());
        orcamentoExistente.setNomeConsultor(novoOrcamento.getNomeConsultor());
        orcamentoExistente.setObservacoes(novoOrcamento.getObservacoes());
        orcamentoExistente.setStatus(novoOrcamento.getStatus());
        orcamentoExistente.getServicosOrcados().clear();
        if (novoOrcamento.getServicosOrcados() != null) {
            orcamentoExistente.getServicosOrcados().addAll(novoOrcamento.getServicosOrcados());
        }
        
        orcamentoExistente.getPecasOrcadas().clear();
        if (novoOrcamento.getPecasOrcadas() != null) {
            orcamentoExistente.getPecasOrcadas().addAll(novoOrcamento.getPecasOrcadas());
        }
        
        orcamentoExistente.setDescontoServicos(novoOrcamento.getDescontoServicos());
        orcamentoExistente.setDescontoPecas(novoOrcamento.getDescontoPecas());
        
        orcamentoExistente.calcularTodosOsPrecos();
        
        orcamentoExistente.setNumeroOrcamento(numeroOrcamentoOriginal);
        orcamentoExistente.setCreatedAt(createdAtOriginal);
        
        Orcamento orcamentoAtualizado = repository.save(orcamentoExistente);
        logger.info("Orçamento atualizado com sucesso: " + orcamentoAtualizado.getNumeroOrcamento());
        return orcamentoAtualizado;
    }

    @Override
    public void deletar(Long id) {
        Orcamento orcamento = buscarPorId(id);
        repository.delete(orcamento);
        logger.info("Orçamento deletado: " + orcamento.getNumeroOrcamento());
    }
}