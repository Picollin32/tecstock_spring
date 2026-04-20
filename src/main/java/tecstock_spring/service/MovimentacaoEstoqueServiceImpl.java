package tecstock_spring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.dao.DataIntegrityViolationException;
import tecstock_spring.model.Fornecedor;
import tecstock_spring.model.MovimentacaoEstoque;
import tecstock_spring.model.NotaEntrada;
import tecstock_spring.model.Peca;
import tecstock_spring.repository.FornecedorRepository;
import tecstock_spring.repository.MovimentacaoEstoqueRepository;
import tecstock_spring.repository.NotaEntradaRepository;
import tecstock_spring.repository.PecaRepository;
import tecstock_spring.util.TenantContext;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovimentacaoEstoqueServiceImpl implements MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final PecaRepository pecaRepository;
    private final ContaService contaService;
    private final FornecedorRepository fornecedorRepository;
    private final NotaEntradaRepository notaEntradaRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(MovimentacaoEstoqueServiceImpl.class);

    private Long requireEmpresaId() {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("ID da empresa não encontrado no contexto");
        }
        return empresaId;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @SuppressWarnings("null")
    public MovimentacaoEstoque registrarEntrada(String codigoPeca, Long fornecedorId, int quantidade, Double precoUnitario, String numeroNotaFiscal, String observacoes, String origem) {
        logger.info("Registrando entrada de estoque - Código: " + codigoPeca + ", Fornecedor ID: " + fornecedorId + ", Quantidade: " + quantidade + ", Preço: " + precoUnitario + ", Nota: " + numeroNotaFiscal);
        Long empresaId = requireEmpresaId();
        if (origem != null && origem.equalsIgnoreCase("ORCAMENTO")) {
            logger.warn("Tentativa de registrar entrada de estoque com origem 'ORCAMENTO' - operação bloqueada.");
            throw new RuntimeException("Operação de movimentação de estoque não permitida para orçamentos");
        }

        if (movimentacaoEstoqueRepository.existsByNumeroNotaFiscalAndFornecedorIdAndEmpresaId(numeroNotaFiscal, fornecedorId, empresaId)) {
            throw new RuntimeException("O número da nota fiscal '" + numeroNotaFiscal + "' já foi utilizado em outra movimentação para este fornecedor.");
        }

        Optional<Peca> pecaOptional = pecaRepository.findByCodigoFabricanteAndFornecedorIdAndEmpresaId(codigoPeca, fornecedorId, empresaId);
        if (pecaOptional.isEmpty()) {
            throw new RuntimeException("Não foi encontrada uma peça cadastrada com o código '" + codigoPeca + "' para o fornecedor informado.");
        }
        
        Peca peca = pecaOptional.get();
        
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
        if (fornecedor.getEmpresa() != null && !fornecedor.getEmpresa().getId().equals(empresaId)) {
            throw new RuntimeException("Fornecedor não pertence à empresa atual");
        }
        
        boolean precoAlterado = false;
        if (precoUnitario != null && Math.abs(peca.getPrecoUnitario() - precoUnitario) > 0.01) {
            logger.info("Atualizando preço da peça de " + peca.getPrecoUnitario() + " para " + precoUnitario);
            peca.setPrecoUnitario(precoUnitario);
            precoAlterado = true;
        }
        
        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setEmpresa(peca.getEmpresa());
        movimentacao.setCodigoPeca(codigoPeca);
        movimentacao.setFornecedor(fornecedor);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setPrecoUnitario(precoUnitario);
        movimentacao.setNumeroNotaFiscal(numeroNotaFiscal);
        movimentacao.setTipoMovimentacao(MovimentacaoEstoque.TipoMovimentacao.ENTRADA);
        movimentacao.setObservacoes(observacoes);
        
        MovimentacaoEstoque movimentacaoSalva = movimentacaoEstoqueRepository.save(movimentacao);

        int linhasAfetadas = pecaRepository.incrementarEstoqueAtomico(peca.getId(), quantidade, empresaId);
        if (linhasAfetadas == 0) {
            logger.error("Falha ao atualizar estoque da peça ID: " + peca.getId());
            throw new RuntimeException("Erro ao atualizar estoque da peça. A peça pode ter sido removida.");
        }

        Peca pecaAtualizada = pecaRepository.findById(peca.getId())
                .orElseThrow(() -> new RuntimeException("Peça não encontrada após atualização de estoque"));
        
        if (precoAlterado) {
            logger.info("Preço da peça atualizado. Novo preço de custo: " + peca.getPrecoUnitario() + ", Novo preço de venda: " + peca.getPrecoFinal());
        }
        logger.info("Entrada registrada com sucesso. Novo estoque da peça: " + pecaAtualizada.getQuantidadeEstoque());
        
        return movimentacaoSalva;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @SuppressWarnings("null")
    public MovimentacaoEstoque registrarSaida(String codigoPeca, Long fornecedorId, int quantidade, String numeroNotaFiscal, String observacoes, String origem) {
        logger.info("Registrando saída de estoque - Código: " + codigoPeca + ", Fornecedor ID: " + fornecedorId + ", Quantidade: " + quantidade + ", Nota: " + numeroNotaFiscal);
        Long empresaId = requireEmpresaId();
        if (origem != null && origem.equalsIgnoreCase("ORCAMENTO")) {
            logger.warn("Tentativa de registrar saída de estoque com origem 'ORCAMENTO' - operação bloqueada.");
            throw new RuntimeException("Operação de movimentação de estoque não permitida para orçamentos");
        }

        if (numeroNotaFiscal == null || numeroNotaFiscal.trim().isEmpty()) {
            numeroNotaFiscal = "SAIDA-" + System.currentTimeMillis() + "-" + codigoPeca;
            logger.info("Nota fiscal gerada automaticamente para saída: " + numeroNotaFiscal);
        } else {

            if (movimentacaoEstoqueRepository.existsByNumeroNotaFiscalAndFornecedorIdAndEmpresaId(numeroNotaFiscal, fornecedorId, empresaId)) {
                throw new RuntimeException("O número da nota fiscal '" + numeroNotaFiscal + "' já foi utilizado em outra movimentação para este fornecedor.");
            }
        }
        
        Optional<Peca> pecaOptional = pecaRepository.findByCodigoFabricanteAndFornecedorIdAndEmpresaId(codigoPeca, fornecedorId, empresaId);
        if (pecaOptional.isEmpty()) {
            throw new RuntimeException("Não foi encontrada uma peça cadastrada com o código '" + codigoPeca + "' para o fornecedor informado.");
        }
        
        Peca peca = pecaOptional.get();
        

        if (peca.getQuantidadeEstoque() < quantidade) {
            throw new RuntimeException("Estoque insuficiente. Disponível: " + peca.getQuantidadeEstoque() + ", Solicitado: " + quantidade);
        }
        
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
        if (fornecedor.getEmpresa() != null && !fornecedor.getEmpresa().getId().equals(empresaId)) {
            throw new RuntimeException("Fornecedor não pertence à empresa atual");
        }
        
        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setEmpresa(peca.getEmpresa());
        movimentacao.setCodigoPeca(codigoPeca);
        movimentacao.setFornecedor(fornecedor);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setPrecoUnitario(peca.getPrecoUnitario());
        movimentacao.setNumeroNotaFiscal(numeroNotaFiscal);
        movimentacao.setTipoMovimentacao(MovimentacaoEstoque.TipoMovimentacao.SAIDA);
        movimentacao.setObservacoes(observacoes);
        
        MovimentacaoEstoque movimentacaoSalva;
        try {
            movimentacaoSalva = movimentacaoEstoqueRepository.save(movimentacao);
        } catch (DataIntegrityViolationException e) {

            logger.error("Violação de constraint ao salvar movimentação de saída: " + e.getMessage());
            throw new RuntimeException("O número da nota fiscal '" + numeroNotaFiscal + 
                "' já foi utilizado em outra movimentação para este fornecedor. " +
                "(Validação garantida pelo banco de dados)");
        }

        int linhasAfetadas = pecaRepository.decrementarEstoqueAtomico(peca.getId(), quantidade, empresaId);
        if (linhasAfetadas == 0) {

            Peca pecaAtualizada = pecaRepository.findById(peca.getId())
                    .orElseThrow(() -> new RuntimeException("Peça não encontrada"));
            throw new RuntimeException("Estoque insuficiente ou peça removida durante a operação. Disponível: " + 
                    pecaAtualizada.getQuantidadeEstoque() + ", Solicitado: " + quantidade);
        }

        Peca pecaAtualizada = pecaRepository.findById(peca.getId())
                .orElseThrow(() -> new RuntimeException("Peça não encontrada após atualização de estoque"));
        
        logger.info("Saída registrada com sucesso. Novo estoque da peça: " + pecaAtualizada.getQuantidadeEstoque());
        
        return movimentacaoSalva;
    }

    @Override
    public List<MovimentacaoEstoque> listarTodas() {
        Long empresaId = requireEmpresaId();
        
        return movimentacaoEstoqueRepository.findByEmpresaId(empresaId);
    }

    @Override
    public List<MovimentacaoEstoque> listarPorCodigoPeca(String codigoPeca) {
        Long empresaId = requireEmpresaId();
        return movimentacaoEstoqueRepository.findByCodigoPecaAndEmpresaIdOrderByDataEntradaDesc(codigoPeca, empresaId);
    }

    @Override
    public List<MovimentacaoEstoque> listarPorFornecedor(Long fornecedorId) {
        Long empresaId = requireEmpresaId();
        return movimentacaoEstoqueRepository.findByFornecedorIdAndEmpresaIdOrderByDataEntradaDesc(fornecedorId, empresaId);
    }

    @Override
    public MovimentacaoEstoque buscarPorId(Long id) {
        Long empresaId = requireEmpresaId();
        
        return movimentacaoEstoqueRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada"));
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @SuppressWarnings("null")
    public void processarSaidaPorOrdemServico(String codigoPeca, Long fornecedorId, int quantidade, String numeroOS) {
        logger.info("MÉTODO CHAMADO: processarSaidaPorOrdemServico");
        logger.info("INICIANDO processamento de saída - Peça: " + codigoPeca + 
                   " | Quantidade: " + quantidade + " | OS: " + numeroOS + " | Fornecedor ID: " + fornecedorId);
        Long empresaId = requireEmpresaId();
        
        try {
            Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId)
                    .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
            if (fornecedor.getEmpresa() != null && !fornecedor.getEmpresa().getId().equals(empresaId)) {
                throw new RuntimeException("Fornecedor não pertence à empresa atual");
            }
            
            Optional<Peca> pecaOptional = pecaRepository.findByCodigoFabricanteAndFornecedorIdAndEmpresaId(codigoPeca, fornecedorId, empresaId);
            if (pecaOptional.isEmpty()) {
                throw new RuntimeException("Peça não encontrada com código: " + codigoPeca + " para o fornecedor informado");
            }
            
            Peca peca = pecaOptional.get();
            int estoqueAnterior = peca.getQuantidadeEstoque();
            logger.info("Peça encontrada: " + peca.getNome() + " | Estoque atual: " + estoqueAnterior);

            if (estoqueAnterior < quantidade) {
                throw new RuntimeException("Estoque insuficiente para a peça " + peca.getNome() + 
                                         ". Disponível: " + estoqueAnterior + 
                                         ", Solicitado: " + quantidade);
            }

            int linhasAfetadas = pecaRepository.decrementarEstoqueAtomico(peca.getId(), quantidade, empresaId);
            if (linhasAfetadas == 0) {

                Peca pecaAtualizada = pecaRepository.findById(peca.getId())
                        .orElseThrow(() -> new RuntimeException("Peça não encontrada"));
                throw new RuntimeException("Estoque insuficiente ou peça removida durante a operação. Disponível: " + 
                        pecaAtualizada.getQuantidadeEstoque() + ", Solicitado: " + quantidade);
            }

            Peca pecaAtualizada = pecaRepository.findById(peca.getId())
                    .orElseThrow(() -> new RuntimeException("Peça não encontrada após atualização"));
            int novoEstoque = pecaAtualizada.getQuantidadeEstoque();
            
            logger.info("ESTOQUE ATUALIZADO - Peça: " + peca.getNome() + 
                       " | Estoque anterior: " + estoqueAnterior + 
                       " | Quantidade subtraída: " + quantidade + 
                       " | Novo estoque: " + novoEstoque);
            
            String numeroNotaFiscal = "OS-" + numeroOS + "-SAIDA-" + codigoPeca;
            MovimentacaoEstoque movimentacaoSaida = new MovimentacaoEstoque();
            movimentacaoSaida.setEmpresa(peca.getEmpresa());
            movimentacaoSaida.setCodigoPeca(codigoPeca);
            movimentacaoSaida.setFornecedor(fornecedor);
            movimentacaoSaida.setQuantidade(quantidade);
            movimentacaoSaida.setPrecoUnitario(peca.getPrecoUnitario());
            movimentacaoSaida.setNumeroNotaFiscal(numeroNotaFiscal);
            movimentacaoSaida.setTipoMovimentacao(MovimentacaoEstoque.TipoMovimentacao.SAIDA);
            movimentacaoSaida.setObservacoes("Saída por fechamento da OS " + numeroOS + " - Peça: " + peca.getNome());

            
            MovimentacaoEstoque movimentacaoSalva = movimentacaoEstoqueRepository.save(movimentacaoSaida);
            logger.info("MOVIMENTAÇÃO REGISTRADA - ID: " + movimentacaoSalva.getId() + 
                       " | Tipo: SAIDA | Peça: " + codigoPeca + 
                       " | Quantidade: " + quantidade + 
                       " | Preço Unitário: R$ " + String.format("%.2f", peca.getPrecoUnitario()) +
                       " | Nota Fiscal: " + numeroNotaFiscal + 
                       " | OS: " + numeroOS);
            
            logger.info("SUCESSO - Saída processada completamente para peça " + peca.getNome() + 
                       " (OS: " + numeroOS + ")");
            
        } catch (Exception e) {
            logger.error("ERRO ao processar saída da peça " + codigoPeca + " (OS: " + numeroOS + "): " + e.getMessage());
            throw new RuntimeException("Erro ao processar saída da peça: " + e.getMessage());
        }
    }

    @Override
    public List<MovimentacaoEstoque> listarPorOrdemServico(String numeroOS) {
        Long empresaId = requireEmpresaId();
        return movimentacaoEstoqueRepository.findByEmpresaId(empresaId).stream()
                .filter(m -> m.getObservacoes() != null && m.getObservacoes().contains("OS " + numeroOS))
                .toList();
    }
    
    @Override
    @Transactional
    @SuppressWarnings("null")
    public void removerSaidasDeOrdemServico(String numeroOS) {
        logger.info("Removendo movimentações de saída antigas da OS: " + numeroOS);
        Long empresaId = requireEmpresaId();

        List<MovimentacaoEstoque> movimentacoes = movimentacaoEstoqueRepository.findByEmpresaId(empresaId).stream()
            .filter(m -> m.getTipoMovimentacao() == MovimentacaoEstoque.TipoMovimentacao.SAIDA &&
                        m.getObservacoes() != null && 
                        m.getObservacoes().contains("OS " + numeroOS))
            .toList();
        
        if (!movimentacoes.isEmpty()) {
            logger.info("Encontradas " + movimentacoes.size() + " movimentações antigas. Devolvendo peças ao estoque...");

            for (MovimentacaoEstoque mov : movimentacoes) {
                Optional<Peca> pecaOpt = pecaRepository.findByCodigoFabricanteAndFornecedorIdAndEmpresaId(
                    mov.getCodigoPeca(), 
                    mov.getFornecedor().getId(),
                    empresaId
                );
                
                if (pecaOpt.isPresent()) {
                    Peca peca = pecaOpt.get();
                    int estoqueAnterior = peca.getQuantidadeEstoque();
                    
                    pecaRepository.incrementarEstoqueAtomico(peca.getId(), mov.getQuantidade(), empresaId);
                    
                    Peca pecaAtualizada = pecaRepository.findById(peca.getId())
                        .orElseThrow(() -> new RuntimeException("Peça não encontrada"));
                    
                    logger.info("  Devolvida peça " + peca.getNome() + 
                               " | Qtd: " + mov.getQuantidade() + 
                               " | Estoque: " + estoqueAnterior + " -> " + pecaAtualizada.getQuantidadeEstoque());
                }
            }

            movimentacaoEstoqueRepository.deleteAll(movimentacoes);
            logger.info("Movimentações antigas removidas e estoque restaurado");
        } else {
            logger.info("Nenhuma movimentação antiga encontrada para a OS " + numeroOS);
        }
    }
    
    @Override
    public boolean verificarNotaFiscalJaUtilizada(String numeroNotaFiscal, Long fornecedorId) {
        Long empresaId = requireEmpresaId();
        return movimentacaoEstoqueRepository.existsByNumeroNotaFiscalAndFornecedorIdAndEmpresaId(numeroNotaFiscal, fornecedorId, empresaId);
    }
    
    @SuppressWarnings("null")
    @Override
    @Transactional
    public List<Map<String, Object>> listarNotasEntrada() {
        Long empresaId = requireEmpresaId();
        List<NotaEntrada> notas = notaEntradaRepository.findByEmpresaIdOrderByDataEntradaDesc(empresaId);

        if (notas.isEmpty()) {
            List<MovimentacaoEstoque> entradas = movimentacaoEstoqueRepository
                    .findByEmpresaIdAndTipoMovimentacaoOrderByDataEntradaDesc(
                            empresaId, MovimentacaoEstoque.TipoMovimentacao.ENTRADA);

            Map<String, List<MovimentacaoEstoque>> grouped = new LinkedHashMap<>();
            for (MovimentacaoEstoque m : entradas) {
                String key = m.getFornecedor().getId() + "||" + m.getNumeroNotaFiscal();
                grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(m);
            }

            for (List<MovimentacaoEstoque> movs : grouped.values()) {
                MovimentacaoEstoque first = movs.get(0);
                double valorTotal = movs.stream().mapToDouble(m -> {
                    double pu = m.getPrecoUnitario() != null ? m.getPrecoUnitario() : 0.0;
                    return m.getQuantidade() * pu;
                }).sum();

                NotaEntrada nota = NotaEntrada.builder()
                        .empresa(first.getEmpresa())
                        .fornecedor(first.getFornecedor())
                        .numeroNotaFiscal(first.getNumeroNotaFiscal())
                        .dataEntrada(first.getDataEntrada() != null ? first.getDataEntrada() : java.time.LocalDateTime.now())
                        .valorTotalCompra(valorTotal)
                        .observacoes(first.getObservacoes())
                        .build();
                notaEntradaRepository.save(nota);
            }

            notas = notaEntradaRepository.findByEmpresaIdOrderByDataEntradaDesc(empresaId);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < notas.size(); i++) {
            NotaEntrada nota = Objects.requireNonNull(notas.get(i));
            List<MovimentacaoEstoque> movs = movimentacaoEstoqueRepository
                    .findByNumeroNotaFiscalAndFornecedorIdAndEmpresaIdAndTipoMovimentacao(
                            nota.getNumeroNotaFiscal(),
                            nota.getFornecedor().getId(),
                            empresaId,
                            MovimentacaoEstoque.TipoMovimentacao.ENTRADA);

            List<Map<String, Object>> itens = movs.stream().map(m -> {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", m.getId());
                item.put("codigoPeca", m.getCodigoPeca());
                item.put("quantidade", m.getQuantidade());
                item.put("precoUnitario", m.getPrecoUnitario());
                item.put("valorItem", m.getQuantidade() * (m.getPrecoUnitario() != null ? m.getPrecoUnitario() : 0.0));
                item.put("dataEntrada", m.getDataEntrada());
                return item;
            }).collect(Collectors.toList());

            Map<String, Object> notaResp = new LinkedHashMap<>();
            Map<String, Object> fornecedorResp = new LinkedHashMap<>();
            fornecedorResp.put("id", nota.getFornecedor().getId());
            fornecedorResp.put("nome", nota.getFornecedor().getNome());
            fornecedorResp.put("cnpj", nota.getFornecedor().getCnpj());

            notaResp.put("id", nota.getId());
            notaResp.put("numeroNotaFiscal", nota.getNumeroNotaFiscal());
            notaResp.put("fornecedor", fornecedorResp);
            notaResp.put("dataEntrada", nota.getDataEntrada());
            notaResp.put("valorTotal", nota.getValorTotalCompra());
            notaResp.put("valorFrete", nota.getValorTotalFrete());
            notaResp.put("formaPagamento", nota.getFormaPagamento());
            notaResp.put("observacoes", nota.getObservacoes());
            notaResp.put("itens", itens);
            notaResp.put("contas", contaService.buscarContasCompra(nota.getNumeroNotaFiscal()));
            result.add(notaResp);
        }
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> atualizarNota(Long fornecedorId, String numeroNotaAtual, Map<String, Object> dados) {
        Long empresaId = requireEmpresaId();

        List<MovimentacaoEstoque> movimentacoes = movimentacaoEstoqueRepository
                .findByNumeroNotaFiscalAndFornecedorIdAndEmpresaIdAndTipoMovimentacao(
                        numeroNotaAtual, fornecedorId, empresaId, MovimentacaoEstoque.TipoMovimentacao.ENTRADA);

        if (movimentacoes.isEmpty()) {
            throw new RuntimeException("Nota fiscal não encontrada: " + numeroNotaAtual);
        }

        String novoNumero = dados.get("novoNumero") != null ? dados.get("novoNumero").toString().trim() : null;
        @SuppressWarnings("unchecked")
        Map<String, Object> pagamento = dados.get("pagamento") instanceof Map
                ? (Map<String, Object>) dados.get("pagamento") : null;

        String numeroVigente = numeroNotaAtual;

        if (novoNumero != null && !novoNumero.equals(numeroNotaAtual)) {
            boolean exists = movimentacaoEstoqueRepository
                    .existsByNumeroNotaFiscalAndFornecedorIdAndEmpresaId(novoNumero, fornecedorId, empresaId);
            if (exists) {
                throw new RuntimeException("O número de nota '" + novoNumero + "' já está em uso para este fornecedor.");
            }
            for (MovimentacaoEstoque m : movimentacoes) {
                m.setNumeroNotaFiscal(novoNumero);
                movimentacaoEstoqueRepository.save(m);
            }
            contaService.atualizarNumeroNotaEmContas(numeroNotaAtual, novoNumero);

            notaEntradaRepository.findByNumeroNotaFiscalAndFornecedorIdAndEmpresaId(numeroNotaAtual, fornecedorId, empresaId)
                    .ifPresent(n -> {
                        n.setNumeroNotaFiscal(novoNumero);
                        notaEntradaRepository.save(n);
                    });

            logger.info("Número da nota fiscal atualizado: {} → {} (fornecedor {})", numeroNotaAtual, novoNumero, fornecedorId);
            numeroVigente = novoNumero;
        }

        if (pagamento != null) {
            double valorTotal = movimentacoes.stream().mapToDouble(m -> {
                double pu = m.getPrecoUnitario() != null ? m.getPrecoUnitario() : 0.0;
                return m.getQuantidade() * pu;
            }).sum();
            contaService.deletarContasCompra(numeroVigente);
            String descNota = "Compra NF " + numeroVigente;
            String formaPagamento = pagamento.getOrDefault("formaPagamento", "AVISTA").toString();
            if (!"AVISTA".equals(formaPagamento)) {
                contaService.gerarContasParaCompra(pagamento, valorTotal, descNota);
            } else {
                contaService.gerarContasParaCompra(pagamento, valorTotal, descNota);
            }
            logger.info("Pagamento da nota {} atualizado: {}", numeroVigente, formaPagamento);

            notaEntradaRepository.findByNumeroNotaFiscalAndFornecedorIdAndEmpresaId(numeroVigente, fornecedorId, empresaId)
                    .ifPresent(n -> {
                        n.setFormaPagamento(formaPagamento);
                        notaEntradaRepository.save(n);
                    });
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("sucesso", true);
        response.put("mensagem", "Nota atualizada com sucesso");
        response.put("numeroNotaFiscal", numeroVigente);
        return response;
    }

    @Override
    @Transactional
    public void deletarNota(Long fornecedorId, String numeroNota) {
        Long empresaId = requireEmpresaId();

        List<MovimentacaoEstoque> movimentacoes = movimentacaoEstoqueRepository
                .findByNumeroNotaFiscalAndFornecedorIdAndEmpresaIdAndTipoMovimentacao(
                        numeroNota, fornecedorId, empresaId, MovimentacaoEstoque.TipoMovimentacao.ENTRADA);

        if (movimentacoes.isEmpty()) {
            throw new RuntimeException("Nota fiscal não encontrada: " + numeroNota);
        }

        Map<String, Long> pecasAfetadas = movimentacoes.stream()
            .collect(Collectors.toMap(
                MovimentacaoEstoque::getCodigoPeca,
                m -> m.getFornecedor().getId(),
                (a, b) -> a,
                LinkedHashMap::new));

        for (MovimentacaoEstoque m : movimentacoes) {
            Optional<Peca> pecaOpt = pecaRepository.findByCodigoFabricanteAndFornecedorIdAndEmpresaId(
                    m.getCodigoPeca(), fornecedorId, empresaId);
            if (pecaOpt.isPresent()) {
                Peca peca = pecaOpt.get();
                if (peca.getQuantidadeEstoque() < m.getQuantidade()) {
                    throw new RuntimeException(
                            "Estoque insuficiente para reverter a entrada da peça '" + m.getCodigoPeca()
                            + "'. Atual: " + peca.getQuantidadeEstoque()
                            + ", Necessário: " + m.getQuantidade());
                }
                pecaRepository.decrementarEstoqueAtomico(peca.getId(), m.getQuantidade(), empresaId);
                logger.info("Estoque revertido para peça {}: -{}", m.getCodigoPeca(), m.getQuantidade());
            }
        }

        movimentacaoEstoqueRepository.deleteAll(movimentacoes);

        for (Map.Entry<String, Long> entry : pecasAfetadas.entrySet()) {
            String codigoPeca = entry.getKey();
            Long fornId = entry.getValue();

            Optional<Peca> pecaOpt = pecaRepository.findByCodigoFabricanteAndFornecedorIdAndEmpresaId(
                    codigoPeca, fornId, empresaId);
            if (pecaOpt.isEmpty()) {
                continue;
            }

            Peca peca = pecaOpt.get();
            Optional<MovimentacaoEstoque> ultimaEntradaRemanescente = movimentacaoEstoqueRepository
                    .findByCodigoPecaAndFornecedorIdAndEmpresaIdOrderByDataEntradaDesc(codigoPeca, fornId, empresaId)
                    .stream()
                    .filter(m -> m.getTipoMovimentacao() == MovimentacaoEstoque.TipoMovimentacao.ENTRADA)
                    .findFirst();

            if (ultimaEntradaRemanescente.isPresent()) {
                Double precoAnterior = ultimaEntradaRemanescente.get().getPrecoUnitario();
                if (precoAnterior != null && Math.abs(peca.getPrecoUnitario() - precoAnterior) > 0.01) {
                    peca.setPrecoUnitario(precoAnterior);
                    pecaRepository.save(peca);
                    logger.info("Preço restaurado para peça {}: {}", codigoPeca, precoAnterior);
                }
            }
        }

        contaService.deletarContasCompra(numeroNota);
        notaEntradaRepository.deleteByNumeroNotaFiscalAndFornecedorIdAndEmpresaId(numeroNota, fornecedorId, empresaId);
        logger.info("Nota de entrada {} excluída: {} movimentação(ões) removida(s)", numeroNota, movimentacoes.size());
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @SuppressWarnings("null")
    public MovimentacaoEstoque registrarEntradaSemValidacaoNota(String codigoPeca, Long fornecedorId, int quantidade, Double precoUnitario, String numeroNotaFiscal, String observacoes) {
        logger.info("Registrando entrada SEM validação de nota - Código: " + codigoPeca + ", Fornecedor ID: " + fornecedorId + ", Quantidade: " + quantidade);
        Long empresaId = requireEmpresaId();
        
        Optional<Peca> pecaOptional = pecaRepository.findByCodigoFabricanteAndFornecedorIdAndEmpresaId(codigoPeca, fornecedorId, empresaId);
        if (pecaOptional.isEmpty()) {
            throw new RuntimeException("Não foi encontrada uma peça cadastrada com o código '" + codigoPeca + "' para o fornecedor informado.");
        }
        
        Peca peca = pecaOptional.get();
        
        Long fornecedorIdNaoNulo = Objects.requireNonNull(fornecedorId, "fornecedorId é obrigatório");

        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorIdNaoNulo)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
        if (fornecedor.getEmpresa() != null && !fornecedor.getEmpresa().getId().equals(empresaId)) {
            throw new RuntimeException("Fornecedor não pertence à empresa atual");
        }
        
        boolean precoAlterado = false;
        if (precoUnitario != null && Math.abs(peca.getPrecoUnitario() - precoUnitario) > 0.01) {
            logger.info("Atualizando preço da peça de " + peca.getPrecoUnitario() + " para " + precoUnitario);
            peca.setPrecoUnitario(precoUnitario);
            precoAlterado = true;
        }
        
        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setEmpresa(peca.getEmpresa());
        movimentacao.setCodigoPeca(codigoPeca);
        movimentacao.setFornecedor(fornecedor);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setPrecoUnitario(precoUnitario);
        movimentacao.setNumeroNotaFiscal(numeroNotaFiscal);
        movimentacao.setTipoMovimentacao(MovimentacaoEstoque.TipoMovimentacao.ENTRADA);
        movimentacao.setObservacoes(observacoes);
        
        MovimentacaoEstoque movimentacaoSalva = movimentacaoEstoqueRepository.save(movimentacao);

        int linhasAfetadas = pecaRepository.incrementarEstoqueAtomico(peca.getId(), quantidade, empresaId);
        if (linhasAfetadas == 0) {
            logger.error("Falha ao atualizar estoque da peça ID: " + peca.getId());
            throw new RuntimeException("Erro ao atualizar estoque da peça. A peça pode ter sido removida.");
        }

        Peca pecaAtualizada = pecaRepository.findById(peca.getId())
                .orElseThrow(() -> new RuntimeException("Peça não encontrada após atualização de estoque"));
        
        if (precoAlterado) {
            logger.info("Preço da peça atualizado. Novo preço de custo: " + peca.getPrecoUnitario() + ", Novo preço de venda: " + peca.getPrecoFinal());
        }
        logger.info("Entrada registrada com sucesso. Novo estoque da peça: " + pecaAtualizada.getQuantidadeEstoque());
        
        return movimentacaoSalva;
    }

    @Override
    @Transactional
    public void registrarOuAtualizarNotaEntrada(
            Long fornecedorId,
            String numeroNotaFiscal,
            String observacoes,
            String formaPagamento,
            double valorTotalCompra,
            Double valorFrete
    ) {
        Long empresaId = requireEmpresaId();
        Long fornecedorIdNaoNulo = Objects.requireNonNull(fornecedorId, "fornecedorId é obrigatório");

        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorIdNaoNulo)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
        if (fornecedor.getEmpresa() != null && !fornecedor.getEmpresa().getId().equals(empresaId)) {
            throw new RuntimeException("Fornecedor não pertence à empresa atual");
        }

        NotaEntrada nota = notaEntradaRepository
            .findByNumeroNotaFiscalAndFornecedorIdAndEmpresaId(numeroNotaFiscal, fornecedorIdNaoNulo, empresaId)
                .orElseGet(() -> NotaEntrada.builder()
                        .empresa(fornecedor.getEmpresa())
                        .fornecedor(fornecedor)
                        .numeroNotaFiscal(numeroNotaFiscal)
                        .dataEntrada(java.time.LocalDateTime.now())
                        .build());

        nota.setObservacoes(observacoes);
        nota.setFormaPagamento(formaPagamento);
        nota.setValorTotalCompra(valorTotalCompra);
        nota.setValorTotalFrete(valorFrete);
        notaEntradaRepository.save(nota);
    }
}
