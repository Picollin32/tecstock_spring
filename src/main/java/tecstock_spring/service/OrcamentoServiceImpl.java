package tecstock_spring.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tecstock_spring.exception.OrcamentoNotFoundException;
import tecstock_spring.model.*;
import tecstock_spring.repository.EmpresaRepository;
import tecstock_spring.repository.OrcamentoRepository;
import tecstock_spring.util.TenantContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrcamentoServiceImpl implements OrcamentoService {

    private final OrcamentoRepository repository;
    private final OrdemServicoService ordemServicoService;
    private final EmpresaRepository empresaRepository;
    private static final Logger logger = LoggerFactory.getLogger(OrcamentoServiceImpl.class);

    @Override
    public Orcamento salvar(Orcamento orcamento) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("ID da empresa não encontrado no contexto");
        }
        
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        orcamento.setEmpresa(empresa);
        
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
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("ID da empresa não encontrado no contexto");
        }
        
        return repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new OrcamentoNotFoundException("Orçamento não encontrado com ID: " + id));
    }
    
    @Override
    public Orcamento buscarPorNumeroOrcamento(String numeroOrcamento) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        return repository.findByNumeroOrcamentoAndEmpresaId(numeroOrcamento, empresaId)
                .orElseThrow(() -> new OrcamentoNotFoundException("Orçamento não encontrado com número: " + numeroOrcamento));
    }

    @Override
    public List<Orcamento> listarTodos() {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("ID da empresa não encontrado no contexto");
        }
        
        List<Orcamento> orcamentos = repository.findByEmpresaId(empresaId);
        logger.info(orcamentos.size() + " orçamentos encontrados (ordenados por numeroOrcamento crescente).");
        return orcamentos;
    }
    
    @Override
    public List<Orcamento> listarPorCliente(String clienteCpf) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        return repository.findByClienteCpfAndEmpresaIdOrderByDataHoraDesc(clienteCpf, empresaId);
    }
    
    @Override
    public List<Orcamento> listarPorVeiculo(String veiculoPlaca) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        return repository.findByVeiculoPlacaAndEmpresaIdOrderByDataHoraDesc(veiculoPlaca, empresaId);
    }
    
    @Override
    public List<Orcamento> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        return repository.findByDataHoraBetweenAndEmpresaId(inicio, fim, empresaId);
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
        orcamentoExistente.setPrazoFiadoDias(novoOrcamento.getPrazoFiadoDias());
        orcamentoExistente.setMecanico(novoOrcamento.getMecanico());
        orcamentoExistente.setConsultor(novoOrcamento.getConsultor());
        orcamentoExistente.setObservacoes(novoOrcamento.getObservacoes());
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
    @SuppressWarnings("null")
    public void deletar(Long id) {
        Orcamento orcamento = buscarPorId(id);
        repository.delete(orcamento);
        logger.info("Orçamento deletado: " + orcamento.getNumeroOrcamento());
    }
    
    @Override
    public OrdemServico transformarEmOrdemServico(Long orcamentoId) {
        Orcamento orcamento = buscarPorId(orcamentoId);
        
        if (Boolean.TRUE.equals(orcamento.getTransformadoEmOS())) {
            throw new IllegalStateException("Este orçamento já foi transformado em OS: " + orcamento.getNumeroOSGerado());
        }
        
        logger.info("Transformando orçamento " + orcamento.getNumeroOrcamento() + " em Ordem de Serviço");

        OrdemServico ordemServico = OrdemServico.builder()
                .dataHora(LocalDateTime.now())
                .clienteNome(orcamento.getClienteNome())
                .clienteCpf(orcamento.getClienteCpf())
                .clienteTelefone(orcamento.getClienteTelefone())
                .clienteEmail(orcamento.getClienteEmail())
                .veiculoNome(orcamento.getVeiculoNome())
                .veiculoMarca(orcamento.getVeiculoMarca())
                .veiculoAno(orcamento.getVeiculoAno())
                .veiculoCor(orcamento.getVeiculoCor())
                .veiculoPlaca(orcamento.getVeiculoPlaca())
                .veiculoQuilometragem(orcamento.getVeiculoQuilometragem())
                .veiculoCategoria(orcamento.getVeiculoCategoria())
                .queixaPrincipal(orcamento.getQueixaPrincipal())
                .mecanico(orcamento.getMecanico())
                .consultor(orcamento.getConsultor())
                .numeroParcelas(orcamento.getNumeroParcelas())
                .servicosRealizados(new ArrayList<>())
                .pecasUtilizadas(new ArrayList<>())
                .descontoServicos(orcamento.getDescontoServicos())
                .descontoPecas(orcamento.getDescontoPecas())
                .garantiaMeses(orcamento.getGarantiaMeses())
                .prazoFiadoDias(orcamento.getPrazoFiadoDias())
                .tipoPagamento(orcamento.getTipoPagamento())
                .observacoes(orcamento.getObservacoes())
                .orcamentoOrigemId(orcamento.getId())
                .numeroOrcamentoOrigem(orcamento.getNumeroOrcamento())
                .status("Aberta")
                .build();
        
        if (orcamento.getServicosOrcados() != null) {
            ordemServico.getServicosRealizados().addAll(orcamento.getServicosOrcados());
        }

        if (orcamento.getPecasOrcadas() != null) {
            for (PecaOrcamento pecaOrc : orcamento.getPecasOrcadas()) {
                PecaOrdemServico pecaOS = new PecaOrdemServico();
                pecaOS.setPeca(pecaOrc.getPeca());
                pecaOS.setQuantidade(pecaOrc.getQuantidade());
                pecaOS.setValorUnitario(pecaOrc.getValorUnitario());
                pecaOS.setValorTotal(pecaOrc.getValorTotal());
                ordemServico.getPecasUtilizadas().add(pecaOS);
            }
        }

        OrdemServico osSalva = ordemServicoService.salvar(ordemServico);
        logger.info("Ordem de Serviço criada: " + osSalva.getNumeroOS() + " a partir do orçamento: " + orcamento.getNumeroOrcamento());

        orcamento.setTransformadoEmOS(true);
        orcamento.setNumeroOSGerado(osSalva.getNumeroOS());
        repository.save(orcamento);
        logger.info("Orçamento " + orcamento.getNumeroOrcamento() + " marcado como transformado");
        
        return osSalva;
    }
}