package tecstock_spring.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import tecstock_spring.controller.PecaController;
import tecstock_spring.exception.CodigoPecaDuplicadoException;
import tecstock_spring.exception.PecaComEstoqueException;
import tecstock_spring.model.Fabricante;
import tecstock_spring.model.Fornecedor;
import tecstock_spring.model.OrdemServico;
import tecstock_spring.model.Peca;
import tecstock_spring.model.PecaOrdemServico;
import tecstock_spring.repository.FabricanteRepository;
import tecstock_spring.repository.FornecedorRepository;
import tecstock_spring.repository.OrdemServicoRepository;
import tecstock_spring.repository.PecaRepository;
import lombok.RequiredArgsConstructor;

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
    
    Logger logger = Logger.getLogger(PecaController.class);

    @Override
    public Peca salvar(Peca peca) {
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
        logger.info("Validando código duplicado para peça: " + peca.getCodigoFabricante());
        logger.info("Fornecedor da peça: " + (peca.getFornecedor() != null ? peca.getFornecedor().getId() + " - " + peca.getFornecedor().getNome() : "null"));
        
        List<Peca> pecasComMesmoCodigo = pecaRepository.findByCodigoFabricante(peca.getCodigoFabricante());
        logger.info("Encontradas " + pecasComMesmoCodigo.size() + " peças com código " + peca.getCodigoFabricante());
        for (Peca p : pecasComMesmoCodigo) {
            logger.info("  Peça ID " + p.getId() + ", fornecedor: " + (p.getFornecedor() != null ? p.getFornecedor().getId() + " - " + p.getFornecedor().getNome() : "null"));
        }
        
        Optional<Peca> pecaExistente;
        
        if (peca.getFornecedor() != null && peca.getFornecedor().getId() != null) {
            logger.info("Buscando peça com código " + peca.getCodigoFabricante() + " e fornecedor ID " + peca.getFornecedor().getId());
            pecaExistente = pecaRepository.findByCodigoFabricanteAndFornecedorId(
                peca.getCodigoFabricante(), peca.getFornecedor().getId());
        } else {
            logger.info("Buscando peça com código " + peca.getCodigoFabricante() + " e sem fornecedor");
            pecaExistente = pecaRepository.findByCodigoFabricanteAndFornecedorIsNull(
                peca.getCodigoFabricante());
        }

        logger.info("Peça existente encontrada: " + pecaExistente.isPresent());
        if (pecaExistente.isPresent()) {
            logger.info("Peça existente ID: " + pecaExistente.get().getId() + ", Peça atual ID: " + peca.getId());
        }

        if (pecaExistente.isPresent() && !pecaExistente.get().getId().equals(peca.getId())) {
            String fornecedorInfo = peca.getFornecedor() != null ? 
                "fornecedor " + peca.getFornecedor().getNome() : "sem fornecedor";
            logger.error("ERRO: Código duplicado detectado!");
            throw new CodigoPecaDuplicadoException(
                "Já existe uma peça cadastrada com o código '" + peca.getCodigoFabricante() + 
                "' para o " + fornecedorInfo + ". Não é possível cadastrar o mesmo código de peça " +
                "para o mesmo fornecedor."
            );
        }
        
        logger.info("Validação de código duplicado concluída com sucesso");
    }

    @Override
    public Peca buscarPorId(Long id) {
        return pecaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Peça não encontrada"));
    }

    @Override
    public Peca buscarPorCodigo(String codigo) {
        return pecaRepository.findByCodigoFabricante(codigo).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Peça com código " + codigo + " não encontrada"));
    }

    @Override
    public List<Peca> listarTodas() {
        List<Peca> pecas = pecaRepository.findAll();
        if (pecas.isEmpty()) {
            logger.info("Nenhuma peça cadastrada.");
        } else {
            logger.info(pecas.size() + " peças encontradas.");
        }
        return pecas;
    }

    @Override
    public Peca atualizar(Long id, Peca novaPeca) {
        Peca pecaExistente = buscarPorId(id);
        
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
    public List<Peca> listarEmUso() {
        logger.info("Listando peças em uso em OSs não encerradas");
        List<Peca> pecas = pecaRepository.findAll();
        return pecas.stream()
                .filter(peca -> peca.getUnidadesUsadasEmOS() != null && peca.getUnidadesUsadasEmOS() > 0)
                .collect(Collectors.toList());
    }
    
    @Override
    public void atualizarUnidadesUsadas() {
        logger.info("Atualizando unidades usadas em OSs para todas as peças");
        
        List<Peca> todasPecas = pecaRepository.findAll();
        List<OrdemServico> osNaoEncerradas = ordemServicoRepository.findByStatusNot("Encerrada");
        
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
            
            pecaRepository.atualizarUnidadesUsadasSemTriggerUpdate(peca.getId(), unidadesUsadas);
            
            if (unidadesUsadas > 0) {
                logger.info("Peça '" + peca.getNome() + "' tem " + unidadesUsadas + " unidades em OSs não encerradas");
            }
        }
        
        logger.info("Atualização de unidades usadas concluída");
    }
}