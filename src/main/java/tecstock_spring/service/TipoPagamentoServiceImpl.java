package tecstock_spring.service;

import java.util.List;

import jakarta.persistence.EntityManager;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tecstock_spring.exception.NomeDuplicadoException;
import tecstock_spring.exception.TipoPagamentoEmUsoException;
import tecstock_spring.model.TipoPagamento;
import tecstock_spring.repository.TipoPagamentoRepository;
import tecstock_spring.repository.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoPagamentoServiceImpl implements TipoPagamentoService {

    private final TipoPagamentoRepository repository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final EntityManager entityManager;
    Logger logger = Logger.getLogger(TipoPagamentoServiceImpl.class);

    @Override
    @Transactional
    public TipoPagamento salvar(TipoPagamento tipoPagamento) {

        tipoPagamento.setId(null);
        
        if (repository.existsByNome(tipoPagamento.getNome())) {
            throw new NomeDuplicadoException("Nome de tipo de pagamento já cadastrado");
        }

        Long maxId = repository.findMaxId();
        if (maxId > 0) {
            entityManager.createNativeQuery(
                "SELECT setval('tipo_pagamento_id_seq', :maxId, true)"
            ).setParameter("maxId", maxId).getSingleResult();
        }
        
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
        
        BeanUtils.copyProperties(novoTipoPagamento, tipoPagamentoExistente, "id", "codigo", "createdAt", "updatedAt");
        return repository.save(tipoPagamentoExistente);
    }

    @Override
    public void deletar(Long id) {

        TipoPagamento tipoPagamento = buscarPorId(id);
        
        boolean temOSVinculada = ordemServicoRepository.findAll().stream()
                .anyMatch(os -> os.getTipoPagamento() != null && os.getTipoPagamento().getId().equals(id));
        
        if (temOSVinculada) {
            throw new TipoPagamentoEmUsoException("Não é possível excluir o tipo de pagamento '" + tipoPagamento.getNome() + "' pois está sendo utilizado em uma ou mais ordens de serviço");
        }
        
        repository.deleteById(id);
        logger.info("Tipo de pagamento excluído com sucesso: " + tipoPagamento.getNome());
    }
}
