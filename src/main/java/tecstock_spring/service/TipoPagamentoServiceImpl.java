package tecstock_spring.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tecstock_spring.exception.NomeDuplicadoException;
import tecstock_spring.model.TipoPagamento;
import tecstock_spring.repository.TipoPagamentoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoPagamentoServiceImpl implements TipoPagamentoService {

    private final TipoPagamentoRepository repository;
    Logger logger = Logger.getLogger(TipoPagamentoServiceImpl.class);

    @Override
    public TipoPagamento salvar(TipoPagamento tipoPagamento) {
        if (repository.existsByNome(tipoPagamento.getNome())) {
            throw new NomeDuplicadoException("Nome de tipo de pagamento já cadastrado");
        }
        
        // Gera o próximo código automaticamente
        Integer proximoCodigo = repository.findMaxCodigo() + 1;
        tipoPagamento.setCodigo(proximoCodigo);
        
        TipoPagamento tipoPagamentoSalvo = repository.save(tipoPagamento);
        logger.info("Tipo de pagamento salvo com sucesso: " + tipoPagamentoSalvo);
        return tipoPagamentoSalvo;
    }

    @Override
    public TipoPagamento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de pagamento não encontrado"));
    }

    @Override
    public List<TipoPagamento> listarTodos() {
        List<TipoPagamento> tiposPagamento = repository.findAll();
        if (tiposPagamento.isEmpty()) {
            logger.info("Nenhum tipo de pagamento cadastrado.");
        } else {
            logger.info(tiposPagamento.size() + " tipos de pagamento encontrados.");
        }
        return tiposPagamento;
    }

    @Override
    public TipoPagamento atualizar(Long id, TipoPagamento novoTipoPagamento) {
        TipoPagamento tipoPagamentoExistente = buscarPorId(id);
        
        if (repository.existsByNomeAndIdNot(novoTipoPagamento.getNome(), id)) {
            throw new NomeDuplicadoException("Nome de tipo de pagamento já cadastrado");
        }
        
        // Preserva o código original na atualização
        BeanUtils.copyProperties(novoTipoPagamento, tipoPagamentoExistente, "id", "codigo", "createdAt", "updatedAt");
        return repository.save(tipoPagamentoExistente);
    }

    @Override
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
