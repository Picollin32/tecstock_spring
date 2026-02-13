package tecstock_spring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tecstock_spring.exception.OrdemServicoNotFoundException;
import tecstock_spring.model.Empresa;
import tecstock_spring.model.OrdemServico;
import tecstock_spring.repository.EmpresaRepository;
import tecstock_spring.repository.OrdemServicoRepository;
import tecstock_spring.repository.PecaRepository;
import tecstock_spring.repository.PecaOrdemServicoRepository;
import tecstock_spring.util.TenantContext;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrdemServicoServiceImpl implements OrdemServicoService {

    private final OrdemServicoRepository repository;
    private final PecaRepository pecaRepository;
    private final PecaOrdemServicoRepository pecaOrdemServicoRepository;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;
    private final ServicoOrdemServicoService servicoOrdemServicoService;
    private final ServicoService servicoService;
    private final PecaService pecaService;
    private final ChecklistService checklistService;
    private final EmpresaRepository empresaRepository;
    private final OrcamentoService orcamentoService;
    private static final Logger logger = LoggerFactory.getLogger(OrdemServicoServiceImpl.class);

    public OrdemServicoServiceImpl(
            OrdemServicoRepository repository,
            PecaRepository pecaRepository,
            PecaOrdemServicoRepository pecaOrdemServicoRepository,
            MovimentacaoEstoqueService movimentacaoEstoqueService,
            ServicoOrdemServicoService servicoOrdemServicoService,
            ServicoService servicoService,
            PecaService pecaService,
            ChecklistService checklistService,
            EmpresaRepository empresaRepository,
            @Lazy OrcamentoService orcamentoService) {
        this.repository = repository;
        this.pecaRepository = pecaRepository;
        this.pecaOrdemServicoRepository = pecaOrdemServicoRepository;
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
        this.servicoOrdemServicoService = servicoOrdemServicoService;
        this.servicoService = servicoService;
        this.pecaService = pecaService;
        this.checklistService = checklistService;
        this.empresaRepository = empresaRepository;
        this.orcamentoService = orcamentoService;
    }

    @Override
    public OrdemServico salvar(OrdemServico ordemServico) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("ID da empresa não encontrado no contexto");
        }
        
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        ordemServico.setEmpresa(empresa);
        
        boolean isNovaOS = ordemServico.getId() == null;

        if (ordemServico.getChecklistId() == null && ordemServico.getOrcamentoOrigemId() == null) {
            logger.error("Tentativa de salvar OS sem checklist vinculado");
            throw new IllegalArgumentException("Não é permitido salvar uma Ordem de Serviço sem um Checklist vinculado. Por favor, selecione um checklist antes de salvar.");
        }
        
        if (ordemServico.getChecklistId() != null) {
            logger.info("Validação de checklist: OK - Checklist ID: " + ordemServico.getChecklistId());
        } else {
            logger.info("OS criada a partir do orçamento " + ordemServico.getNumeroOrcamentoOrigem() + " - Checklist será exigido ao encerrar");
        }
        
        if (isNovaOS && (ordemServico.getNumeroOS() == null || ordemServico.getNumeroOS().isEmpty())) {
            Integer max = repository.findAll().stream()
                .filter(os -> os.getNumeroOS() != null && os.getNumeroOS().matches("\\d+"))
                .mapToInt(os -> Integer.parseInt(os.getNumeroOS()))
                .max()
                .orElse(0);
            ordemServico.setNumeroOS(String.valueOf(max + 1));
            logger.info("Gerando novo número de OS: " + ordemServico.getNumeroOS());
        }
        
        processarEstoquePecas(ordemServico, isNovaOS);

        ordemServico.forcarRecalculoTodosOsPrecos();
        logger.info("Preços calculados - Serviços: R$ " + ordemServico.getPrecoTotalServicos() + 
                   ", Peças: R$ " + ordemServico.getPrecoTotalPecas() + 
                   ", Total: R$ " + ordemServico.getPrecoTotal());
        
        OrdemServico ordemServicoSalva = repository.save(ordemServico);
        logger.info("Ordem de Serviço salva com sucesso: " + ordemServicoSalva.getNumeroOS());

        logger.info("Atualizando contadores de serviços e peças em uso");
        servicoService.atualizarUnidadesUsadas();
        pecaService.atualizarUnidadesUsadas();
        
        return ordemServicoSalva;
    }

    @Override
    public OrdemServico buscarPorId(Long id) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("ID da empresa não encontrado no contexto");
        }
        
        return repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new OrdemServicoNotFoundException("Ordem de Serviço não encontrada com ID: " + id));
    }
    
    @Override
    public OrdemServico buscarPorNumeroOS(String numeroOS) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("ID da empresa não encontrado no contexto");
        }
        return repository.findByNumeroOSAndEmpresaId(numeroOS, empresaId)
                .orElseThrow(() -> new OrdemServicoNotFoundException("Ordem de Serviço não encontrada com número: " + numeroOS));
    }

    @Override
    public List<OrdemServico> listarTodos() {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("ID da empresa não encontrado no contexto");
        }
        
        List<OrdemServico> ordensServico = repository.findByEmpresaId(empresaId);
        logger.info(ordensServico.size() + " ordens de serviço encontradas (ordenadas por numeroOS crescente).");
        return ordensServico;
    }
    
    @Override
    public List<OrdemServico> listarPorCliente(String clienteCpf) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        return repository.findByClienteCpfAndEmpresaIdOrderByDataHoraDesc(clienteCpf, empresaId);
    }
    
    @Override
    public List<OrdemServico> listarPorVeiculo(String veiculoPlaca) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        return repository.findByVeiculoPlacaAndEmpresaIdOrderByDataHoraDesc(veiculoPlaca, empresaId);
    }
    
    @Override
    public List<OrdemServico> listarPorStatus(String status) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        return repository.findByStatusAndEmpresaIdOrderByDataHoraDesc(status, empresaId);
    }
    
    @Override
    public List<OrdemServico> listarPorChecklist(Long checklistId) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        return repository.findByChecklistIdAndEmpresaIdOrderByDataHoraDesc(checklistId, empresaId);
    }
    
    @Override
    public List<OrdemServico> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        return repository.findByDataHoraBetweenAndEmpresaId(inicio, fim, empresaId);
    }
    
    @Override
    public List<OrdemServico> pesquisarPorNumeroExato(String numero) {
        try {
            OrdemServico os = buscarPorNumeroOS(numero);
            logger.info("Ordem de serviço encontrada com número exato: " + numero);
            return List.of(os);
        } catch (OrdemServicoNotFoundException e) {
            logger.info("Nenhuma ordem de serviço encontrada com número: " + numero);
            return List.of();
        } catch (Exception e) {
            logger.error("Erro ao pesquisar ordem de serviço por número " + numero + ": " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public OrdemServico atualizar(Long id, OrdemServico novaOrdemServico) {
        OrdemServico ordemServicoExistente = buscarPorId(id);

        if (novaOrdemServico.getChecklistId() == null && ordemServicoExistente.getOrcamentoOrigemId() == null) {
            logger.error("Tentativa de atualizar OS ID: " + id + " sem checklist vinculado");
            throw new IllegalArgumentException("Não é permitido atualizar uma Ordem de Serviço sem um Checklist vinculado. Por favor, selecione um checklist antes de salvar.");
        }
        
        if (novaOrdemServico.getChecklistId() != null) {
            logger.info("Validação de checklist na atualização: OK - Checklist ID: " + novaOrdemServico.getChecklistId());
        } else {
            logger.info("OS proveniente de orçamento - Checklist será exigido ao encerrar");
        }

        List<tecstock_spring.model.PecaOrdemServico> pecasAnteriores = new java.util.ArrayList<>();
        if (ordemServicoExistente.getPecasUtilizadas() != null) {
            for (tecstock_spring.model.PecaOrdemServico peca : ordemServicoExistente.getPecasUtilizadas()) {
                pecasAnteriores.add(peca);
            }
        }
        
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
        ordemServicoExistente.setPrazoFiadoDias(novaOrdemServico.getPrazoFiadoDias());
        ordemServicoExistente.setFiadoPago(novaOrdemServico.getFiadoPago());
        ordemServicoExistente.setMecanico(novaOrdemServico.getMecanico());
        ordemServicoExistente.setConsultor(novaOrdemServico.getConsultor());
        ordemServicoExistente.setObservacoes(novaOrdemServico.getObservacoes());
        ordemServicoExistente.setStatus(novaOrdemServico.getStatus());
        ordemServicoExistente.setDescontoServicos(novaOrdemServico.getDescontoServicos());
        ordemServicoExistente.setDescontoPecas(novaOrdemServico.getDescontoPecas());
        logger.info("Descontos atualizados - Serviços: R$ " + ordemServicoExistente.getDescontoServicos() + 
                   ", Peças: R$ " + ordemServicoExistente.getDescontoPecas());
        
        ordemServicoExistente.getServicosRealizados().clear();
        if (novaOrdemServico.getServicosRealizados() != null) {
            ordemServicoExistente.getServicosRealizados().addAll(novaOrdemServico.getServicosRealizados());
        }
        
        processarDiferencasEstoque(pecasAnteriores, novaOrdemServico.getPecasUtilizadas());

        ordemServicoExistente.getPecasUtilizadas().clear();
        if (novaOrdemServico.getPecasUtilizadas() != null) {
            for (tecstock_spring.model.PecaOrdemServico pecaNova : novaOrdemServico.getPecasUtilizadas()) {
                tecstock_spring.model.PecaOrdemServico novaPecaOS = new tecstock_spring.model.PecaOrdemServico();

                if (pecaNova.getId() != null) {

                    @SuppressWarnings("null")
                    java.util.Optional<tecstock_spring.model.PecaOrdemServico> pecaExistente = 
                        pecaOrdemServicoRepository.findById(pecaNova.getId());
                    if (pecaExistente.isPresent()) {
                        novaPecaOS = pecaExistente.get();
                        novaPecaOS.setQuantidade(pecaNova.getQuantidade());
                        
                        if (novaPecaOS.getValorUnitario() == null && pecaNova.getValorUnitario() != null) {
                            novaPecaOS.setValorUnitario(pecaNova.getValorUnitario());
                        }

                        if (novaPecaOS.getValorUnitario() != null) {
                            novaPecaOS.setValorTotal(novaPecaOS.getValorUnitario() * novaPecaOS.getQuantidade());
                        }
                    }
                } else {
                    novaPecaOS.setPeca(pecaNova.getPeca());
                    novaPecaOS.setQuantidade(pecaNova.getQuantidade());
                    novaPecaOS.setValorUnitario(pecaNova.getValorUnitario());
                    novaPecaOS.setValorTotal(pecaNova.getValorTotal());
                }
                
                ordemServicoExistente.getPecasUtilizadas().add(novaPecaOS);
            }
        }

        ordemServicoExistente.setNumeroOS(numeroOSOriginal);
        ordemServicoExistente.setCreatedAt(createdAtOriginal);

        ordemServicoExistente.forcarRecalculoTodosOsPrecos();
        logger.info("Preços recalculados na atualização - Serviços: R$ " + ordemServicoExistente.getPrecoTotalServicos() + 
                   ", Peças: R$ " + ordemServicoExistente.getPrecoTotalPecas() + 
                   ", Total: R$ " + ordemServicoExistente.getPrecoTotal());
        
        logger.info("Atualizando Ordem de Serviço ID: " + id + " - Preservando número: " + numeroOSOriginal);
        OrdemServico ordemServicoAtualizada = repository.save(ordemServicoExistente);

        logger.info("Atualizando contadores de serviços e peças em uso");
        servicoService.atualizarUnidadesUsadas();
        pecaService.atualizarUnidadesUsadas();
        
        return ordemServicoAtualizada;
    }
    
    @Override
    public OrdemServico atualizarApenasStatus(Long id, String novoStatus) {
        OrdemServico ordemServico = buscarPorId(id);
        ordemServico.setStatus(novoStatus);

        if ("Encerrada".equalsIgnoreCase(novoStatus)) {
            ordemServico.setDataHoraEncerramento(LocalDateTime.now());
            logger.info("Registrando data/hora de encerramento: " + LocalDateTime.now());
        }
        
        logger.info("Atualizando apenas status da OS ID: " + id + " para: " + novoStatus);
        return repository.save(ordemServico);
    }

    @Override
    @Transactional
    public OrdemServico fecharOrdemServico(Long id) {
        logger.info("INICIANDO FECHAMENTO DA OS - ID: " + id);
        
        OrdemServico ordemServico = buscarPorId(id);
        logger.info("OS encontrada - Número: " + ordemServico.getNumeroOS() + 
                   " | Status atual: " + ordemServico.getStatus() +
                   " | Peças utilizadas: " + (ordemServico.getPecasUtilizadas() != null ? ordemServico.getPecasUtilizadas().size() : "0"));
        
        if ("Encerrada".equals(ordemServico.getStatus())) {
            logger.warn("Tentativa de fechar OS já encerrada: " + ordemServico.getNumeroOS());
            return ordemServico;
        }

        if (ordemServico.getChecklistId() == null) {
            logger.error("Tentativa de fechar OS ID: " + id + " (Número: " + ordemServico.getNumeroOS() + ") sem checklist vinculado");
            throw new IllegalArgumentException("Não é permitido fechar uma Ordem de Serviço sem um Checklist vinculado. Por favor, vincule um checklist antes de fechar a OS.");
        }
        
        logger.info("Validação de checklist no fechamento: OK - Checklist ID: " + ordemServico.getChecklistId());
        logger.info("OS válida para fechamento. Prosseguindo...");

        ordemServico.setStatus("Encerrada");
        ordemServico.setDataHoraEncerramento(LocalDateTime.now());
        logger.info("Status da OS alterado para 'Encerrada'");
        logger.info("Data/hora de encerramento registrada: " + LocalDateTime.now());
        logger.info("Iniciando registro dos serviços realizados na OS: " + ordemServico.getNumeroOS());
        servicoOrdemServicoService.registrarServicosRealizados(ordemServico);
        logger.info("Serviços registrados com sucesso");
        logger.info("Verificando e removendo movimentações antigas de estoque da OS: " + ordemServico.getNumeroOS());
        movimentacaoEstoqueService.removerSaidasDeOrdemServico(ordemServico.getNumeroOS());

        if (ordemServico.getPecasUtilizadas() != null && !ordemServico.getPecasUtilizadas().isEmpty()) {
            logger.info("Iniciando processamento de saída para " + ordemServico.getPecasUtilizadas().size() + 
                       " peças da OS " + ordemServico.getNumeroOS());

            if (movimentacaoEstoqueService == null) {
                logger.error("ERRO CRÍTICO: MovimentacaoEstoqueService é NULO!");
                throw new RuntimeException("Serviço de movimentação de estoque não foi injetado");
            }
            logger.info("MovimentacaoEstoqueService injetado corretamente");
            
            int pecasProcessadas = 0;
            for (tecstock_spring.model.PecaOrdemServico pecaOS : ordemServico.getPecasUtilizadas()) {
                if (pecaOS.getPeca() != null) {
                    try {
                        logger.info("Processando peça " + (pecasProcessadas + 1) + "/" + ordemServico.getPecasUtilizadas().size() + 
                                   ": " + pecaOS.getPeca().getNome() + " (Código: " + pecaOS.getPeca().getCodigoFabricante() + 
                                   ", Quantidade: " + pecaOS.getQuantidade() + ")");

                        logger.info("Chamando movimentacaoEstoqueService.processarSaidaPorOrdemServico...");
                        movimentacaoEstoqueService.processarSaidaPorOrdemServico(
                            pecaOS.getPeca().getCodigoFabricante(),
                            pecaOS.getPeca().getFornecedor().getId(),
                            pecaOS.getQuantidade(),
                            ordemServico.getNumeroOS()
                        );
                        
                        pecasProcessadas++;
                        logger.info("Peça " + pecaOS.getPeca().getNome() + " processada com sucesso " +
                                   "(quantidade: " + pecaOS.getQuantidade() + ") para OS " + ordemServico.getNumeroOS());
                    } catch (Exception e) {
                        logger.error("Erro ao processar saída da peça " + pecaOS.getPeca().getNome() + 
                                   " para OS " + ordemServico.getNumeroOS() + ": " + e.getMessage());
                        logger.error("Stack trace:", e);

                    }
                } else {
                    logger.warn("Peça nula encontrada na posição " + (pecasProcessadas + 1) + " da OS " + ordemServico.getNumeroOS());
                }
            }
            
            logger.info("Processamento de saída concluído: " + pecasProcessadas + "/" + ordemServico.getPecasUtilizadas().size() + 
                       " peças processadas com sucesso para OS " + ordemServico.getNumeroOS());
        } else {
            logger.info("Nenhuma peça encontrada para processar saída na OS " + ordemServico.getNumeroOS());
        }
        
    logger.info("Salvando OS com status 'Encerrada'...");
        OrdemServico ordemServicoSalva = repository.save(ordemServico);
    logger.info("Ordem de Serviço encerrada com sucesso: " + ordemServicoSalva.getNumeroOS() + 
                   " | Status final: " + ordemServicoSalva.getStatus());

        if (ordemServicoSalva.getChecklistId() != null) {
            try {
                logger.info("Fechando checklist vinculado ID: " + ordemServicoSalva.getChecklistId());
                boolean checklistFechado = checklistService.fecharChecklist(ordemServicoSalva.getChecklistId());
                if (checklistFechado) {
                    logger.info("Checklist " + ordemServicoSalva.getChecklistId() + " fechado com sucesso");
                } else {
                    logger.warn("Não foi possível fechar o checklist " + ordemServicoSalva.getChecklistId());
                }
            } catch (Exception e) {
                logger.error("Erro ao fechar checklist vinculado: " + e.getMessage());

            }
        }

        logger.info("Atualizando contadores de serviços e peças em uso após encerramento");
        servicoService.atualizarUnidadesUsadas();
        pecaService.atualizarUnidadesUsadas();
        
        return ordemServicoSalva;
    }

    @Override
    @SuppressWarnings("null")
    public void deletar(Long id) {
        OrdemServico ordemServico = buscarPorId(id);

        if ("Encerrada".equals(ordemServico.getStatus()) && 
            ordemServico.getPecasUtilizadas() != null && !ordemServico.getPecasUtilizadas().isEmpty()) {
            
            logger.info("OS está encerrada. Restaurando estoque das peças...");
            for (tecstock_spring.model.PecaOrdemServico pecaOS : ordemServico.getPecasUtilizadas()) {
                try {
                    tecstock_spring.model.Peca peca = pecaOS.getPeca();
                    peca.setQuantidadeEstoque(peca.getQuantidadeEstoque() + pecaOS.getQuantidade());
                    pecaRepository.save(peca);
                    logger.info("Restaurado estoque da peça " + pecaOS.getPeca().getNome() + ": " + pecaOS.getQuantidade() + " unidades");
                } catch (Exception e) {
                    logger.error("Erro ao restaurar estoque da peça " + pecaOS.getPeca().getNome() + ": " + e.getMessage());
                }
            }
        } else if (!"Encerrada".equals(ordemServico.getStatus())) {
            logger.info("OS não está encerrada. Nenhum estoque será restaurado (estoque não foi subtraído).");
        }
        
        if (ordemServico.getOrcamentoOrigemId() != null) {
            try {
                logger.info("OS " + ordemServico.getNumeroOS() + " foi criada a partir do orçamento " + 
                           ordemServico.getNumeroOrcamentoOrigem() + " (ID: " + ordemServico.getOrcamentoOrigemId() + 
                           "). Excluindo orçamento de origem...");
                orcamentoService.deletar(ordemServico.getOrcamentoOrigemId());
                logger.info("Orçamento de origem excluído com sucesso");
            } catch (Exception e) {
                logger.error("Erro ao excluir orçamento de origem ID " + ordemServico.getOrcamentoOrigemId() + ": " + e.getMessage());

            }
        }
        
        logger.info("Deletando Ordem de Serviço: " + ordemServico.getNumeroOS());
        repository.deleteById(id);

        logger.info("Atualizando contadores de serviços e peças em uso após deletar OS");
        servicoService.atualizarUnidadesUsadas();
        pecaService.atualizarUnidadesUsadas();
    }
    
    @Override
    public void processarEstoquePecas(OrdemServico ordemServico, boolean isNovaOS) {
        if (ordemServico.getPecasUtilizadas() == null || ordemServico.getPecasUtilizadas().isEmpty()) {
            return;
        }
        
        logger.info("Processando estoque de peças para OS: " + ordemServico.getNumeroOS() + " (Nova OS: " + isNovaOS + ")");

        logger.info("Estoque das peças não será alterado na criação/atualização da OS. Alteração ocorrerá apenas no fechamento da OS.");
        
        for (tecstock_spring.model.PecaOrdemServico pecaOS : ordemServico.getPecasUtilizadas()) {
            if (pecaOS.getPeca() != null) {

                if (pecaOS.getPeca().getQuantidadeEstoque() < pecaOS.getQuantidade()) {
                    logger.warn("AVISO: Peça " + pecaOS.getPeca().getNome() + 
                               " tem estoque insuficiente. Disponível: " + pecaOS.getPeca().getQuantidadeEstoque() + 
                               ", Necessário: " + pecaOS.getQuantidade());
                } else {
                    logger.info("Peça " + pecaOS.getPeca().getNome() + " validada. Estoque disponível: " + 
                               pecaOS.getPeca().getQuantidadeEstoque() + ", Será utilizado: " + pecaOS.getQuantidade());
                }
            }
        }
    }

    private void processarDiferencasEstoque(List<tecstock_spring.model.PecaOrdemServico> pecasAnteriores, List<tecstock_spring.model.PecaOrdemServico> pecasNovas) {

        logger.info("Processando diferenças de peças na atualização da OS (sem alterar estoque)");
        
        java.util.Map<Long, Integer> pecasAnterioresMap = new java.util.HashMap<>();
        if (pecasAnteriores != null) {
            for (tecstock_spring.model.PecaOrdemServico peca : pecasAnteriores) {
                if (peca.getPeca() != null) {
                    pecasAnterioresMap.put(peca.getPeca().getId(), peca.getQuantidade());
                }
            }
        }
        
        java.util.Map<Long, Integer> pecasNovasMap = new java.util.HashMap<>();
        if (pecasNovas != null) {
            for (tecstock_spring.model.PecaOrdemServico peca : pecasNovas) {
                if (peca.getPeca() != null) {
                    pecasNovasMap.put(peca.getPeca().getId(), peca.getQuantidade());
                }
            }
        }

        java.util.Set<Long> todasAsPecas = new java.util.HashSet<>();
        todasAsPecas.addAll(pecasAnterioresMap.keySet());
        todasAsPecas.addAll(pecasNovasMap.keySet());
        
        for (Long pecaId : todasAsPecas) {
            int quantidadeAnterior = pecasAnterioresMap.getOrDefault(pecaId, 0);
            int quantidadeNova = pecasNovasMap.getOrDefault(pecaId, 0);
            int diferenca = quantidadeNova - quantidadeAnterior;
            
            if (diferenca != 0) {
                @SuppressWarnings("null")
                tecstock_spring.model.Peca peca = pecaRepository.findById(pecaId).orElse(null);
                if (peca != null) {
                    String acao = diferenca > 0 ? "aumentada" : "reduzida";
                    logger.info("Quantidade da peça " + peca.getNome() + " foi " + acao + " em " + Math.abs(diferenca) + 
                               " unidades na OS. Estoque será atualizado apenas no fechamento da OS.");
                }
            }
        }
    }
    
    @Override
    public List<OrdemServico> getFiadosEmAberto() {
        logger.info("Buscando fiados em aberto (OSs encerradas com prazo de fiado definido)");
        Long empresaId = TenantContext.getCurrentEmpresaId();
        List<OrdemServico> fiados = repository.findByStatusAndPrazoFiadoDiasIsNotNullAndEmpresaIdOrderByDataHoraEncerramentoAsc("Encerrada", empresaId);
        logger.info("Encontrados " + fiados.size() + " fiados em aberto");
        return fiados;
    }
    
    @Override
    public OrdemServico marcarFiadoComoPago(Long id, Boolean pago) {
        OrdemServico ordemServico = buscarPorId(id);
        ordemServico.setFiadoPago(pago);
        logger.info("Marcando fiado OS " + ordemServico.getNumeroOS() + " como " + (pago ? "PAGO" : "NÃO PAGO"));
        return repository.save(ordemServico);
    }
    
    @Override
    public OrdemServico desbloquearParaEdicao(Long id) {
        OrdemServico ordemServico = buscarPorId(id);
        logger.info("Desbloqueando OS " + ordemServico.getNumeroOS() + " para edição. Status anterior: " + ordemServico.getStatus());
        logger.info("Dados da OS antes do desbloqueio:");
        logger.info("  - Serviços: " + (ordemServico.getServicosRealizados() != null ? ordemServico.getServicosRealizados().size() : "null"));
        logger.info("  - Peças: " + (ordemServico.getPecasUtilizadas() != null ? ordemServico.getPecasUtilizadas().size() : "null"));
        
        if ("Encerrada".equals(ordemServico.getStatus())) {
            logger.info("OS estava encerrada. Removendo movimentações de saída antigas e devolvendo peças ao estoque...");
            movimentacaoEstoqueService.removerSaidasDeOrdemServico(ordemServico.getNumeroOS());
            logger.info("Movimentações de saída removidas e peças devolvidas ao estoque com sucesso");
        }

        if (ordemServico.getChecklistId() != null) {
            try {
                logger.info("Reabrindo checklist vinculado ID: " + ordemServico.getChecklistId());
                boolean checklistReaberto = checklistService.reabrirChecklist(ordemServico.getChecklistId());
                if (checklistReaberto) {
                    logger.info("Checklist " + ordemServico.getChecklistId() + " reaberto com sucesso");
                } else {
                    logger.warn("Não foi possível reabrir o checklist " + ordemServico.getChecklistId());
                }
            } catch (Exception e) {
                logger.error("Erro ao reabrir checklist vinculado: " + e.getMessage());
            }
        }
        
        ordemServico.setStatus("Em Andamento");
        ordemServico.setDataHoraEncerramento(null);
        
        OrdemServico osSalva = repository.save(ordemServico);

        logger.info("Dados da OS após o desbloqueio:");
        logger.info("  - Serviços: " + (osSalva.getServicosRealizados() != null ? osSalva.getServicosRealizados().size() : "null"));
        logger.info("  - Peças: " + (osSalva.getPecasUtilizadas() != null ? osSalva.getPecasUtilizadas().size() : "null"));
        
        logger.info("Atualizando contadores de serviços e peças em uso após desbloqueio");
        servicoService.atualizarUnidadesUsadas();
        pecaService.atualizarUnidadesUsadas();
        
        logger.info("OS desbloqueada com sucesso. Novo status: " + osSalva.getStatus());
        return osSalva;
    }
    
    @Override
    public OrdemServico reabrirOS(Long id) {
        OrdemServico ordemServico = buscarPorId(id);
        logger.info("Reabrindo OS " + ordemServico.getNumeroOS() + ". Status anterior: " + ordemServico.getStatus());
        logger.info("Dados PRESERVADOS da OS:");
        logger.info("  - Serviços: " + (ordemServico.getServicosRealizados() != null ? ordemServico.getServicosRealizados().size() : "null"));
        logger.info("  - Peças: " + (ordemServico.getPecasUtilizadas() != null ? ordemServico.getPecasUtilizadas().size() : "null"));
        logger.info("  - Preço Total: R$ " + ordemServico.getPrecoTotal());
        logger.info("  - Desconto Serviços: R$ " + ordemServico.getDescontoServicos());
        logger.info("  - Desconto Peças: R$ " + ordemServico.getDescontoPecas());
        
        logger.info("Removendo movimentações de saída antigas e devolvendo peças ao estoque...");
        movimentacaoEstoqueService.removerSaidasDeOrdemServico(ordemServico.getNumeroOS());
        logger.info("Movimentações de saída removidas e peças devolvidas ao estoque com sucesso");

        ordemServico.setStatus("Aberta");
        ordemServico.setDataHoraEncerramento(null);
        
        if (ordemServico.getChecklistId() != null) {
            try {
                logger.info("Reabrindo checklist vinculado ID: " + ordemServico.getChecklistId());
                boolean checklistReaberto = checklistService.reabrirChecklist(ordemServico.getChecklistId());
                if (checklistReaberto) {
                    logger.info("Checklist " + ordemServico.getChecklistId() + " reaberto com sucesso");
                } else {
                    logger.warn("Não foi possível reabrir o checklist " + ordemServico.getChecklistId());
                }
            } catch (Exception e) {
                logger.error("Erro ao reabrir checklist vinculado: " + e.getMessage());

            }
        }

        OrdemServico osSalva = repository.save(ordemServico);

        logger.info("Dados da OS APÓS reabertura:");
        logger.info("  - Serviços: " + (osSalva.getServicosRealizados() != null ? osSalva.getServicosRealizados().size() : "null"));
        logger.info("  - Peças: " + (osSalva.getPecasUtilizadas() != null ? osSalva.getPecasUtilizadas().size() : "null"));
        logger.info("  - Preço Total: R$ " + osSalva.getPrecoTotal());
        logger.info("  - Desconto Serviços: R$ " + osSalva.getDescontoServicos());
        logger.info("  - Desconto Peças: R$ " + osSalva.getDescontoPecas());
        
        logger.info("Atualizando contadores de serviços e peças em uso após reabertura");
        servicoService.atualizarUnidadesUsadas();
        pecaService.atualizarUnidadesUsadas();
        
        logger.info("OS " + osSalva.getNumeroOS() + " reaberta com sucesso. Novo status: " + osSalva.getStatus());
        return osSalva;
    }
    
    @Override
    public Page<OrdemServico> buscarPaginado(String query, String tipo, Pageable pageable) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        if (query == null || query.trim().isEmpty()) {
            return repository.findByEmpresaIdOrderByCreatedAtDesc(empresaId, pageable);
        }

        String tipoBusca = tipo == null ? "numero" : tipo.trim().toLowerCase();
        String termo = query.trim();
        switch (tipoBusca) {
            case "cliente":
                return repository.searchByClienteNomeAndEmpresaId(termo, empresaId, pageable);
            case "placa":
                return repository.searchByVeiculoPlacaAndEmpresaId(termo, empresaId, pageable);
            case "numero":
            default:
                return repository.searchByNumeroOSAndEmpresaId(termo, empresaId, pageable);
        }
    }
    
    @Override
    public List<OrdemServico> listarUltimosParaInicio(int limit) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        Pageable pageable = PageRequest.of(0, limit);
        return repository.findTopByEmpresaIdOrderByCreatedAtDesc(empresaId, pageable);
    }
}
