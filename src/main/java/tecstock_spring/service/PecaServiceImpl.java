package tecstock_spring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import tecstock_spring.controller.PecaController;
import tecstock_spring.dto.AjusteEstoqueDTO;
import tecstock_spring.exception.CodigoPecaDuplicadoException;
import tecstock_spring.exception.PecaComEstoqueException;
import tecstock_spring.model.Empresa;
import tecstock_spring.model.Fabricante;
import tecstock_spring.model.Fornecedor;
import tecstock_spring.model.MovimentacaoEstoque;
import tecstock_spring.model.OrdemServico;
import tecstock_spring.model.Peca;
import tecstock_spring.model.PecaOrdemServico;
import tecstock_spring.repository.EmpresaRepository;
import tecstock_spring.repository.FabricanteRepository;
import tecstock_spring.repository.FornecedorRepository;
import tecstock_spring.repository.MovimentacaoEstoqueRepository;
import tecstock_spring.repository.OrdemServicoRepository;
import tecstock_spring.repository.PecaRepository;
import tecstock_spring.util.TenantContext;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PecaServiceImpl implements PecaService {

    private final PecaRepository pecaRepository;
    private final FabricanteRepository fabricanteRepository; 
    private final FornecedorRepository fornecedorRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final EmpresaRepository empresaRepository;
    
    Logger logger = LoggerFactory.getLogger(PecaController.class);

    @Override
    @SuppressWarnings("null")
    public Peca salvar(Peca peca) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("ID da empresa não encontrado no contexto");
        }
        
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        peca.setEmpresa(empresa);
        
        if (peca.getFabricante() == null || peca.getFabricante().getId() == null) {
            throw new IllegalArgumentException("O ID do Fabricante não pode ser nulo ao salvar uma Peça.");
        }
        Fabricante fabricanteGerenciado = fabricanteRepository.findById(peca.getFabricante().getId())
                .orElseThrow(() -> new RuntimeException("Fabricante com ID " + peca.getFabricante().getId() + " não encontrado."));
        peca.setFabricante(fabricanteGerenciado);

        if (peca.getFornecedor() != null && peca.getFornecedor().getId() != null) {
            Fornecedor fornecedorGerenciado = fornecedorRepository.findById(peca.getFornecedor().getId())
                    .orElseThrow(() -> new RuntimeException("Fornecedor com ID " + peca.getFornecedor().getId() + " não encontrado."));
            peca.setFornecedor(fornecedorGerenciado);
        }

        validarCodigoDuplicado(peca);

        Peca pecaSalva = pecaRepository.save(peca);
        
        logger.info("Peça salva com sucesso: " + pecaSalva);
        return pecaSalva;
    }

    private void validarCodigoDuplicado(Peca peca) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("ID da empresa não encontrado no contexto");
        }

        logger.info("Validando código duplicado para peça: " + peca.getCodigoFabricante());

        Optional<Peca> pecaExistente;

        if (peca.getFornecedor() != null && peca.getFornecedor().getId() != null) {
            logger.info("Buscando peça com código " + peca.getCodigoFabricante() + " e fornecedor ID " + peca.getFornecedor().getId() + " na empresa " + empresaId);
            pecaExistente = pecaRepository.findByCodigoFabricanteAndFornecedorIdAndEmpresaId(
                peca.getCodigoFabricante(), peca.getFornecedor().getId(), empresaId);
        } else {
            logger.info("Buscando peça com código " + peca.getCodigoFabricante() + " sem fornecedor na empresa " + empresaId);
            pecaExistente = pecaRepository.findByCodigoFabricanteAndFornecedorIsNullAndEmpresaId(
                peca.getCodigoFabricante(), empresaId);
        }

        if (pecaExistente.isPresent() && !pecaExistente.get().getId().equals(peca.getId())) {
            String fornecedorInfo = peca.getFornecedor() != null ? 
                "fornecedor " + peca.getFornecedor().getNome() : "sem fornecedor";
            throw new CodigoPecaDuplicadoException(
                "Já existe uma peça cadastrada com o código '" + peca.getCodigoFabricante() + 
                "' para o " + fornecedorInfo + " nesta empresa."
            );
        }
    }

    @Override
    public Peca buscarPorId(Long id) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("ID da empresa não encontrado no contexto");
        }
        
        return pecaRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Peça não encontrada"));
    }

    @Override
    public Peca buscarPorCodigo(String codigo) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("ID da empresa não encontrado no contexto");
        }
        return pecaRepository.findByCodigoFabricanteAndEmpresaId(codigo, empresaId).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Peça com código " + codigo + " não encontrada"));
    }

    @Override
    public List<Peca> listarTodas() {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("ID da empresa não encontrado no contexto");
        }
        
        List<Peca> pecas = pecaRepository.findByEmpresaId(empresaId);
        if (pecas.isEmpty()) {
            logger.info("Nenhuma peça cadastrada.");
        } else {
            logger.info(pecas.size() + " peças encontradas.");
        }
        return pecas;
    }

    @Override
    @SuppressWarnings("null")
    public Peca atualizar(Long id, Peca novaPeca) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("ID da empresa não encontrado no contexto");
        }
        
        Peca pecaExistente = buscarPorId(id);
        
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        novaPeca.setEmpresa(empresa);
        novaPeca.setId(id);
        if (novaPeca.getFabricante() != null && novaPeca.getFabricante().getId() != null) {
            Fabricante fabricanteGerenciado = fabricanteRepository.findById(novaPeca.getFabricante().getId())
                    .orElseThrow(() -> new RuntimeException("Fabricante com ID " + novaPeca.getFabricante().getId() + " não encontrado."));
            novaPeca.setFabricante(fabricanteGerenciado);
        }

        if (novaPeca.getFornecedor() != null && novaPeca.getFornecedor().getId() != null) {
            Fornecedor fornecedorGerenciado = fornecedorRepository.findById(novaPeca.getFornecedor().getId())
                    .orElseThrow(() -> new RuntimeException("Fornecedor com ID " + novaPeca.getFornecedor().getId() + " não encontrado."));
            novaPeca.setFornecedor(fornecedorGerenciado);
        }
        
        validarCodigoDuplicado(novaPeca);
        
        BeanUtils.copyProperties(novaPeca, pecaExistente, "id", "createdAt", "updatedAt");
        return pecaRepository.save(pecaExistente);
    }

    @Override
    @SuppressWarnings("null")
    public void deletar(Long id) {
        Peca peca = buscarPorId(id);
        
        if (peca.getQuantidadeEstoque() > 0) {
            throw new PecaComEstoqueException("Não é possível excluir a peça '" + peca.getNome() + 
                "' pois ela ainda possui " + peca.getQuantidadeEstoque() + 
                " unidades em estoque. Para excluir uma peça, é necessário que seu estoque seja zero.");
        }
        
        logger.info("Excluindo peça com estoque zero: " + peca.getNome());
        pecaRepository.deleteById(id);
    }
    
    @Override
    public Peca ajustarEstoque(AjusteEstoqueDTO ajusteDTO) {
        logger.info("Iniciando ajuste de estoque para peça ID: " + ajusteDTO.getPecaId() + ", ajuste: " + ajusteDTO.getAjuste());

        Peca peca = buscarPorId(ajusteDTO.getPecaId());
        
        if (ajusteDTO.getAjuste() == null || ajusteDTO.getAjuste() == 0) {
            throw new IllegalArgumentException("O valor do ajuste não pode ser zero ou nulo");
        }

        int estoqueAtual = peca.getQuantidadeEstoque();
        int novoEstoque = estoqueAtual + ajusteDTO.getAjuste();
        
        if (novoEstoque < 0) {
            throw new IllegalArgumentException("O ajuste resultaria em estoque negativo (" + novoEstoque + "). Estoque atual: " + estoqueAtual);
        }
        
        peca.setQuantidadeEstoque(novoEstoque);

        double precoAnterior = peca.getPrecoUnitario();
        Double precoNovo = null;
        
        if (ajusteDTO.getNovoPrecoUnitario() != null) {
            if (ajusteDTO.getNovoPrecoUnitario() < 0) {
                throw new IllegalArgumentException("O preço unitário não pode ser negativo");
            }
            logger.info("Atualizando preço unitário de " + precoAnterior + " para " + ajusteDTO.getNovoPrecoUnitario());
            peca.setPrecoUnitario(ajusteDTO.getNovoPrecoUnitario());
            precoNovo = ajusteDTO.getNovoPrecoUnitario();
        }
        
        Peca pecaAtualizada = pecaRepository.save(peca);

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setEmpresa(peca.getEmpresa());
        movimentacao.setCodigoPeca(peca.getCodigoFabricante());
        movimentacao.setFornecedor(peca.getFornecedor());
        movimentacao.setQuantidade(Math.abs(ajusteDTO.getAjuste()));

        String operacao = ajusteDTO.getAjuste() > 0 ? "+" : "-";
        String observacao = "Ajuste de estoque " + operacao + " " + Math.abs(ajusteDTO.getAjuste());
        if (ajusteDTO.getObservacoes() != null && !ajusteDTO.getObservacoes().isEmpty()) {
            observacao += " - " + ajusteDTO.getObservacoes();
        }

        if (precoNovo != null) {
            observacao += String.format(" | Preço reajustado: R$ %.2f → R$ %.2f", precoAnterior, precoNovo);
            movimentacao.setPrecoAnterior(precoAnterior);
            movimentacao.setPrecoNovo(precoNovo);
        }
        
        movimentacao.setObservacoes(observacao);

        String notaFiscal = "REAJUSTE-" + peca.getId() + "-" + System.currentTimeMillis();
        movimentacao.setNumeroNotaFiscal(notaFiscal);

        movimentacao.setTipoMovimentacao(MovimentacaoEstoque.TipoMovimentacao.REAJUSTE);
        movimentacao.setDataEntrada(LocalDateTime.now());
        
        movimentacaoEstoqueRepository.save(movimentacao);
        
        logger.info("Ajuste de estoque concluído. Estoque anterior: " + estoqueAtual + ", novo estoque: " + novoEstoque);
        logger.info("Movimentação registrada: " + observacao);
        
        return pecaAtualizada;
    }
    
    @Override
    public List<Peca> listarEmUso() {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("ID da empresa não encontrado no contexto");
        }
        
        logger.info("Listando peças em uso em OSs não encerradas");
        List<Peca> pecas = pecaRepository.findByEmpresaId(empresaId);
        return pecas.stream()
                .filter(peca -> peca.getUnidadesUsadasEmOS() != null && peca.getUnidadesUsadasEmOS() > 0)
                .collect(Collectors.toList());
    }
    
    @Override
    public void atualizarUnidadesUsadas() {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("ID da empresa não encontrado no contexto");
        }
        
        logger.info("Atualizando unidades usadas em OSs para todas as peças");
        
        List<Peca> todasPecas = pecaRepository.findByEmpresaId(empresaId);
        List<OrdemServico> osNaoEncerradas = ordemServicoRepository.findByStatusNotAndEmpresaId("Encerrada", empresaId);
        
        for (Peca peca : todasPecas) {
            int unidadesUsadas = 0;
            
            for (OrdemServico os : osNaoEncerradas) {
                if (os.getPecasUtilizadas() != null) {
                    for (PecaOrdemServico pecaOS : os.getPecasUtilizadas()) {
                        if (pecaOS.getPeca() != null && pecaOS.getPeca().getId().equals(peca.getId())) {
                            unidadesUsadas += pecaOS.getQuantidade();
                        }
                    }
                }
            }
            
            pecaRepository.atualizarUnidadesUsadasSemTriggerUpdate(peca.getId(), unidadesUsadas, empresaId);
            
            if (unidadesUsadas > 0) {
                logger.info("Peça '" + peca.getNome() + "' tem " + unidadesUsadas + " unidades em OSs não encerradas");
            }
        }
        
        logger.info("Atualização de unidades usadas concluída");
    }
}