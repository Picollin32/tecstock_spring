package tecstock_spring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.dao.DataIntegrityViolationException;
import tecstock_spring.model.Fornecedor;
import tecstock_spring.model.MovimentacaoEstoque;
import tecstock_spring.model.Peca;
import tecstock_spring.repository.FornecedorRepository;
import tecstock_spring.repository.MovimentacaoEstoqueRepository;
import tecstock_spring.repository.PecaRepository;
import tecstock_spring.util.TenantContext;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovimentacaoEstoqueServiceImpl implements MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final PecaRepository pecaRepository;
    private final FornecedorRepository fornecedorRepository;
    
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
}
