package tecstock_spring.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import tecstock_spring.model.Fornecedor;
import tecstock_spring.model.MovimentacaoEstoque;
import tecstock_spring.model.Peca;
import tecstock_spring.repository.FornecedorRepository;
import tecstock_spring.repository.MovimentacaoEstoqueRepository;
import tecstock_spring.repository.PecaRepository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovimentacaoEstoqueServiceImpl implements MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final PecaRepository pecaRepository;
    private final FornecedorRepository fornecedorRepository;
    
    private static final Logger logger = Logger.getLogger(MovimentacaoEstoqueServiceImpl.class);

    @Override
    public MovimentacaoEstoque registrarEntrada(String codigoPeca, Long fornecedorId, int quantidade, Double precoUnitario, String numeroNotaFiscal, String observacoes, String origem) {
        logger.info("Registrando entrada de estoque - Código: " + codigoPeca + ", Fornecedor ID: " + fornecedorId + ", Quantidade: " + quantidade + ", Preço: " + precoUnitario + ", Nota: " + numeroNotaFiscal);
        if (origem != null && origem.equalsIgnoreCase("ORCAMENTO")) {
            logger.warn("Tentativa de registrar entrada de estoque com origem 'ORCAMENTO' - operação bloqueada.");
            throw new RuntimeException("Operação de movimentação de estoque não permitida para orçamentos");
        }

        if (movimentacaoEstoqueRepository.existsByNumeroNotaFiscalAndFornecedorId(numeroNotaFiscal, fornecedorId)) {
            throw new RuntimeException("O número da nota fiscal '" + numeroNotaFiscal + "' já foi utilizado em outra movimentação para este fornecedor.");
        }

        Optional<Peca> pecaOptional = pecaRepository.findByCodigoFabricanteAndFornecedorId(codigoPeca, fornecedorId);
        if (pecaOptional.isEmpty()) {
            throw new RuntimeException("Não foi encontrada uma peça cadastrada com o código '" + codigoPeca + "' para o fornecedor informado.");
        }
        
        Peca peca = pecaOptional.get();
        
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
        
        boolean precoAlterado = false;
        if (precoUnitario != null && Math.abs(peca.getPrecoUnitario() - precoUnitario) > 0.01) {
            logger.info("Atualizando preço da peça de " + peca.getPrecoUnitario() + " para " + precoUnitario);
            peca.setPrecoUnitario(precoUnitario);
            precoAlterado = true;
        }
        
        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setCodigoPeca(codigoPeca);
        movimentacao.setFornecedor(fornecedor);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setPrecoUnitario(precoUnitario);
        movimentacao.setNumeroNotaFiscal(numeroNotaFiscal);
        movimentacao.setTipoMovimentacao(MovimentacaoEstoque.TipoMovimentacao.ENTRADA);
        movimentacao.setObservacoes(observacoes);
        
        MovimentacaoEstoque movimentacaoSalva = movimentacaoEstoqueRepository.save(movimentacao);
        
        peca.setQuantidadeEstoque(peca.getQuantidadeEstoque() + quantidade);
        peca = pecaRepository.save(peca);
        
        if (precoAlterado) {
            logger.info("Preço da peça atualizado. Novo preço de custo: " + peca.getPrecoUnitario() + ", Novo preço de venda: " + peca.getPrecoFinal());
        }
        logger.info("Entrada registrada com sucesso. Novo estoque da peça: " + peca.getQuantidadeEstoque());
        
        return movimentacaoSalva;
    }

    @Override
    public MovimentacaoEstoque registrarSaida(String codigoPeca, Long fornecedorId, int quantidade, String numeroNotaFiscal, String observacoes, String origem) {
        logger.info("Registrando saída de estoque - Código: " + codigoPeca + ", Fornecedor ID: " + fornecedorId + ", Quantidade: " + quantidade + ", Nota: " + numeroNotaFiscal);
        if (origem != null && origem.equalsIgnoreCase("ORCAMENTO")) {
            logger.warn("Tentativa de registrar saída de estoque com origem 'ORCAMENTO' - operação bloqueada.");
            throw new RuntimeException("Operação de movimentação de estoque não permitida para orçamentos");
        }

        if (movimentacaoEstoqueRepository.existsByNumeroNotaFiscalAndFornecedorId(numeroNotaFiscal, fornecedorId)) {
            throw new RuntimeException("O número da nota fiscal '" + numeroNotaFiscal + "' já foi utilizado em outra movimentação para este fornecedor.");
        }
        
        Optional<Peca> pecaOptional = pecaRepository.findByCodigoFabricanteAndFornecedorId(codigoPeca, fornecedorId);
        if (pecaOptional.isEmpty()) {
            throw new RuntimeException("Não foi encontrada uma peça cadastrada com o código '" + codigoPeca + "' para o fornecedor informado.");
        }
        
        Peca peca = pecaOptional.get();
        
        if (peca.getQuantidadeEstoque() < quantidade) {
            throw new RuntimeException("Estoque insuficiente. Disponível: " + peca.getQuantidadeEstoque() + ", Solicitado: " + quantidade);
        }
        
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
        
        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setCodigoPeca(codigoPeca);
        movimentacao.setFornecedor(fornecedor);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setNumeroNotaFiscal(numeroNotaFiscal);
        movimentacao.setTipoMovimentacao(MovimentacaoEstoque.TipoMovimentacao.SAIDA);
        movimentacao.setObservacoes(observacoes);
        
        MovimentacaoEstoque movimentacaoSalva = movimentacaoEstoqueRepository.save(movimentacao);
        
        peca.setQuantidadeEstoque(peca.getQuantidadeEstoque() - quantidade);
        pecaRepository.save(peca);
        
        logger.info("Saída registrada com sucesso. Novo estoque da peça: " + peca.getQuantidadeEstoque());
        
        return movimentacaoSalva;
    }

    @Override
    public List<MovimentacaoEstoque> listarTodas() {
        return movimentacaoEstoqueRepository.findAllByOrderByDataEntradaDesc();
    }

    @Override
    public List<MovimentacaoEstoque> listarPorCodigoPeca(String codigoPeca) {
        return movimentacaoEstoqueRepository.findByCodigoPecaOrderByDataEntradaDesc(codigoPeca);
    }

    @Override
    public List<MovimentacaoEstoque> listarPorFornecedor(Long fornecedorId) {
        return movimentacaoEstoqueRepository.findByFornecedorIdOrderByDataEntradaDesc(fornecedorId);
    }

    @Override
    public MovimentacaoEstoque buscarPorId(Long id) {
        return movimentacaoEstoqueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada"));
    }

    @Override
    public void processarSaidaPorOrdemServico(String codigoPeca, Long fornecedorId, int quantidade, String numeroOS) {
        logger.info("🎯 MÉTODO CHAMADO: processarSaidaPorOrdemServico");
        logger.info("🔄 INICIANDO processamento de saída - Peça: " + codigoPeca + 
                   " | Quantidade: " + quantidade + " | OS: " + numeroOS + " | Fornecedor ID: " + fornecedorId);
        
        try {
            Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId)
                    .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
            
            Optional<Peca> pecaOptional = pecaRepository.findByCodigoFabricanteAndFornecedorId(codigoPeca, fornecedorId);
            if (pecaOptional.isEmpty()) {
                throw new RuntimeException("Peça não encontrada com código: " + codigoPeca + " para o fornecedor informado");
            }
            
            Peca peca = pecaOptional.get();
            logger.info("📦 Peça encontrada: " + peca.getNome() + " | Estoque atual: " + peca.getQuantidadeEstoque());
            
            if (peca.getQuantidadeEstoque() < quantidade) {
                throw new RuntimeException("Estoque insuficiente para a peça " + peca.getNome() + 
                                         ". Disponível: " + peca.getQuantidadeEstoque() + 
                                         ", Solicitado: " + quantidade);
            }

            int estoqueAnterior = peca.getQuantidadeEstoque();
            int novoEstoque = estoqueAnterior - quantidade;
            peca.setQuantidadeEstoque(novoEstoque);
            pecaRepository.save(peca);
            logger.info("📉 ESTOQUE ATUALIZADO - Peça: " + peca.getNome() + 
                       " | Estoque anterior: " + estoqueAnterior + 
                       " | Quantidade subtraída: " + quantidade + 
                       " | Novo estoque: " + novoEstoque);
            
            String numeroNotaFiscal = "OS-" + numeroOS + "-SAIDA-" + codigoPeca;
            MovimentacaoEstoque movimentacaoSaida = new MovimentacaoEstoque();
            movimentacaoSaida.setCodigoPeca(codigoPeca);
            movimentacaoSaida.setFornecedor(fornecedor);
            movimentacaoSaida.setQuantidade(quantidade);
            movimentacaoSaida.setPrecoUnitario(peca.getPrecoUnitario());
            movimentacaoSaida.setNumeroNotaFiscal(numeroNotaFiscal);
            movimentacaoSaida.setTipoMovimentacao(MovimentacaoEstoque.TipoMovimentacao.SAIDA);
            movimentacaoSaida.setObservacoes("Saída por fechamento da OS " + numeroOS + " - Peça: " + peca.getNome());

            
            MovimentacaoEstoque movimentacaoSalva = movimentacaoEstoqueRepository.save(movimentacaoSaida);
            logger.info("📝 MOVIMENTAÇÃO REGISTRADA - ID: " + movimentacaoSalva.getId() + 
                       " | Tipo: SAIDA | Peça: " + codigoPeca + 
                       " | Quantidade: " + quantidade + 
                       " | Preço Unitário: R$ " + String.format("%.2f", peca.getPrecoUnitario()) +
                       " | Nota Fiscal: " + numeroNotaFiscal + 
                       " | OS: " + numeroOS);
            
            logger.info("✅ SUCESSO - Saída processada completamente para peça " + peca.getNome() + 
                       " (OS: " + numeroOS + ")");
            
        } catch (Exception e) {
            logger.error("❌ ERRO ao processar saída da peça " + codigoPeca + " (OS: " + numeroOS + "): " + e.getMessage());
            throw new RuntimeException("Erro ao processar saída da peça: " + e.getMessage());
        }
    }

    @Override
    public List<MovimentacaoEstoque> listarPorOrdemServico(String numeroOS) {
        return movimentacaoEstoqueRepository.findByObservacoesContainingOrderByDataEntradaDesc("OS " + numeroOS);
    }
    
    @Override
    public boolean verificarNotaFiscalJaUtilizada(String numeroNotaFiscal, Long fornecedorId) {
        return movimentacaoEstoqueRepository.existsByNumeroNotaFiscalAndFornecedorId(numeroNotaFiscal, fornecedorId);
    }
    
    @Override
    public MovimentacaoEstoque registrarEntradaSemValidacaoNota(String codigoPeca, Long fornecedorId, int quantidade, Double precoUnitario, String numeroNotaFiscal, String observacoes) {
        logger.info("Registrando entrada SEM validação de nota - Código: " + codigoPeca + ", Fornecedor ID: " + fornecedorId + ", Quantidade: " + quantidade);
        
        Optional<Peca> pecaOptional = pecaRepository.findByCodigoFabricanteAndFornecedorId(codigoPeca, fornecedorId);
        if (pecaOptional.isEmpty()) {
            throw new RuntimeException("Não foi encontrada uma peça cadastrada com o código '" + codigoPeca + "' para o fornecedor informado.");
        }
        
        Peca peca = pecaOptional.get();
        
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
        
        boolean precoAlterado = false;
        if (precoUnitario != null && Math.abs(peca.getPrecoUnitario() - precoUnitario) > 0.01) {
            logger.info("Atualizando preço da peça de " + peca.getPrecoUnitario() + " para " + precoUnitario);
            peca.setPrecoUnitario(precoUnitario);
            precoAlterado = true;
        }
        
        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setCodigoPeca(codigoPeca);
        movimentacao.setFornecedor(fornecedor);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setPrecoUnitario(precoUnitario);
        movimentacao.setNumeroNotaFiscal(numeroNotaFiscal);
        movimentacao.setTipoMovimentacao(MovimentacaoEstoque.TipoMovimentacao.ENTRADA);
        movimentacao.setObservacoes(observacoes);
        
        MovimentacaoEstoque movimentacaoSalva = movimentacaoEstoqueRepository.save(movimentacao);
        
        peca.setQuantidadeEstoque(peca.getQuantidadeEstoque() + quantidade);
        peca = pecaRepository.save(peca);
        
        if (precoAlterado) {
            logger.info("Preço da peça atualizado. Novo preço de custo: " + peca.getPrecoUnitario() + ", Novo preço de venda: " + peca.getPrecoFinal());
        }
        logger.info("Entrada registrada com sucesso. Novo estoque da peça: " + peca.getQuantidadeEstoque());
        
        return movimentacaoSalva;
    }
}
