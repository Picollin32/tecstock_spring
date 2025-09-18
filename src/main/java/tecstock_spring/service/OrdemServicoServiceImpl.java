package tecstock_spring.service;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tecstock_spring.exception.OrdemServicoNotFoundException;
import tecstock_spring.model.OrdemServico;
import tecstock_spring.repository.OrdemServicoRepository;
import tecstock_spring.repository.PecaRepository;
import tecstock_spring.repository.PecaOrdemServicoRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdemServicoServiceImpl implements OrdemServicoService {

    private final OrdemServicoRepository repository;
    private final PecaRepository pecaRepository;
    private final PecaOrdemServicoRepository pecaOrdemServicoRepository;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;
    private static final Logger logger = Logger.getLogger(OrdemServicoServiceImpl.class);

    @Override
    public OrdemServico salvar(OrdemServico ordemServico) {
        boolean isNovaOS = ordemServico.getId() == null;
        
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
                    java.util.Optional<tecstock_spring.model.PecaOrdemServico> pecaExistente = 
                        pecaOrdemServicoRepository.findById(pecaNova.getId());
                    if (pecaExistente.isPresent()) {
                        novaPecaOS = pecaExistente.get();
                        novaPecaOS.setQuantidade(pecaNova.getQuantidade());
                        if (novaPecaOS.getValorUnitario() == null) {
                            novaPecaOS.setValorUnitario(pecaNova.getValorUnitario());
                        }
                        if (novaPecaOS.getValorTotal() == null) {
                            novaPecaOS.setValorTotal(pecaNova.getValorTotal());
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
        return repository.save(ordemServicoExistente);
    }
    
    @Override
    public OrdemServico atualizarApenasStatus(Long id, String novoStatus) {
        OrdemServico ordemServico = buscarPorId(id);
        ordemServico.setStatus(novoStatus);
        logger.info("Atualizando apenas status da OS ID: " + id + " para: " + novoStatus);
        return repository.save(ordemServico);
    }

    @Override
    @Transactional
    public OrdemServico fecharOrdemServico(Long id) {
        logger.info("🔥 INICIANDO FECHAMENTO DA OS - ID: " + id);
        
        OrdemServico ordemServico = buscarPorId(id);
        logger.info("📋 OS encontrada - Número: " + ordemServico.getNumeroOS() + 
                   " | Status atual: " + ordemServico.getStatus() +
                   " | Peças utilizadas: " + (ordemServico.getPecasUtilizadas() != null ? ordemServico.getPecasUtilizadas().size() : "0"));
        
        if ("Encerrada".equals(ordemServico.getStatus())) {
            logger.warn("⚠️ Tentativa de fechar OS já encerrada: " + ordemServico.getNumeroOS());
            return ordemServico;
        }
        
        logger.info("✅ OS válida para fechamento. Prosseguindo...");

    ordemServico.setStatus("Encerrada");
    logger.info("📝 Status da OS alterado para 'Encerrada'");

        if (ordemServico.getPecasUtilizadas() != null && !ordemServico.getPecasUtilizadas().isEmpty()) {
            logger.info("🚀 Iniciando processamento de saída para " + ordemServico.getPecasUtilizadas().size() + 
                       " peças da OS " + ordemServico.getNumeroOS());

            if (movimentacaoEstoqueService == null) {
                logger.error("❌ ERRO CRÍTICO: MovimentacaoEstoqueService é NULO!");
                throw new RuntimeException("Serviço de movimentação de estoque não foi injetado");
            }
            logger.info("✅ MovimentacaoEstoqueService injetado corretamente");
            
            int pecasProcessadas = 0;
            for (tecstock_spring.model.PecaOrdemServico pecaOS : ordemServico.getPecasUtilizadas()) {
                if (pecaOS.getPeca() != null) {
                    try {
                        logger.info("🔄 Processando peça " + (pecasProcessadas + 1) + "/" + ordemServico.getPecasUtilizadas().size() + 
                                   ": " + pecaOS.getPeca().getNome() + " (Código: " + pecaOS.getPeca().getCodigoFabricante() + 
                                   ", Quantidade: " + pecaOS.getQuantidade() + ")");

                        logger.info("📞 Chamando movimentacaoEstoqueService.processarSaidaPorOrdemServico...");
                        movimentacaoEstoqueService.processarSaidaPorOrdemServico(
                            pecaOS.getPeca().getCodigoFabricante(),
                            pecaOS.getPeca().getFornecedor().getId(),
                            pecaOS.getQuantidade(),
                            ordemServico.getNumeroOS()
                        );
                        
                        pecasProcessadas++;
                        logger.info("✅ Peça " + pecaOS.getPeca().getNome() + " processada com sucesso " +
                                   "(quantidade: " + pecaOS.getQuantidade() + ") para OS " + ordemServico.getNumeroOS());
                    } catch (Exception e) {
                        logger.error("❌ Erro ao processar saída da peça " + pecaOS.getPeca().getNome() + 
                                   " para OS " + ordemServico.getNumeroOS() + ": " + e.getMessage());
                        logger.error("❌ Stack trace:", e);

                    }
                } else {
                    logger.warn("⚠️ Peça nula encontrada na posição " + (pecasProcessadas + 1) + " da OS " + ordemServico.getNumeroOS());
                }
            }
            
            logger.info("📊 Processamento de saída concluído: " + pecasProcessadas + "/" + ordemServico.getPecasUtilizadas().size() + 
                       " peças processadas com sucesso para OS " + ordemServico.getNumeroOS());
        } else {
            logger.info("ℹ️ Nenhuma peça encontrada para processar saída na OS " + ordemServico.getNumeroOS());
        }
        
    logger.info("💾 Salvando OS com status 'Encerrada'...");
        OrdemServico ordemServicoSalva = repository.save(ordemServico);
    logger.info("🎉 Ordem de Serviço encerrada com sucesso: " + ordemServicoSalva.getNumeroOS() + 
                   " | Status final: " + ordemServicoSalva.getStatus());
        return ordemServicoSalva;
    }

    @Override
    public void deletar(Long id) {
        OrdemServico ordemServico = buscarPorId(id);

        if ("Encerrada".equals(ordemServico.getStatus()) && 
            ordemServico.getPecasUtilizadas() != null && !ordemServico.getPecasUtilizadas().isEmpty()) {
            
            logger.info("OS está encerrada. Restaurando estoque das peças...");
            for (tecstock_spring.model.PecaOrdemServico pecaOS : ordemServico.getPecasUtilizadas()) {
                try {
                    pecaOS.getPeca().setQuantidadeEstoque(pecaOS.getPeca().getQuantidadeEstoque() + pecaOS.getQuantidade());
                    pecaRepository.save(pecaOS.getPeca());
                    logger.info("Restaurado estoque da peça " + pecaOS.getPeca().getNome() + ": " + pecaOS.getQuantidade() + " unidades");
                } catch (Exception e) {
                    logger.error("Erro ao restaurar estoque da peça " + pecaOS.getPeca().getNome() + ": " + e.getMessage());
                }
            }
        } else if (!"Encerrada".equals(ordemServico.getStatus())) {
            logger.info("OS não está encerrada. Nenhum estoque será restaurado (estoque não foi subtraído).");
        }
        
        logger.info("Deletando Ordem de Serviço: " + ordemServico.getNumeroOS());
        repository.deleteById(id);
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
                tecstock_spring.model.Peca peca = pecaRepository.findById(pecaId).orElse(null);
                if (peca != null) {
                    int novoEstoque = peca.getQuantidadeEstoque() - diferenca;
                    peca.setQuantidadeEstoque(novoEstoque);
                    pecaRepository.save(peca);
                    
                    String acao = diferenca > 0 ? "subtraída" : "adicionada";
                    logger.info("Estoque da peça " + peca.getNome() + " atualizado: " + Math.abs(diferenca) + " " + acao + ", estoque atual: " + novoEstoque);
                }
            }
        }
    }
}
