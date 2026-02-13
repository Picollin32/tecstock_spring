package tecstock_spring.service;

import java.util.List;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tecstock_spring.exception.NomeDuplicadoException;
import tecstock_spring.exception.TipoPagamentoEmUsoException;
import tecstock_spring.model.Empresa;
import tecstock_spring.model.TipoPagamento;
import tecstock_spring.repository.EmpresaRepository;
import tecstock_spring.repository.TipoPagamentoRepository;
import tecstock_spring.repository.OrdemServicoRepository;
import tecstock_spring.util.TenantContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoPagamentoServiceImpl implements TipoPagamentoService {

    private final TipoPagamentoRepository repository;
    private final EmpresaRepository empresaRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final EntityManager entityManager;
    Logger logger = LoggerFactory.getLogger(TipoPagamentoServiceImpl.class);

    @Override
    @Transactional
    public TipoPagamento salvar(TipoPagamento tipoPagamento) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }

        tipoPagamento.setId(null);
        
        if (repository.existsByNomeAndEmpresaId(tipoPagamento.getNome(), empresaId)) {
            throw new NomeDuplicadoException("Nome de tipo de pagamento já cadastrado nesta empresa");
        }

        Empresa empresa = empresaRepository.findById(empresaId)
            .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        tipoPagamento.setEmpresa(empresa);

        Long maxId = repository.findMaxId();
        if (maxId > 0) {
            entityManager.createNativeQuery(
                "SELECT setval('tipo_pagamento_id_seq', :maxId, true)"
            ).setParameter("maxId", maxId).getSingleResult();
        }
        
        Integer proximoCodigo = repository.findMaxCodigo() + 1;
        tipoPagamento.setCodigo(proximoCodigo);
        
        TipoPagamento tipoPagamentoSalvo = repository.save(tipoPagamento);
        logger.info("Tipo de pagamento salvo com sucesso na empresa " + empresaId + ": " + tipoPagamentoSalvo);
        return tipoPagamentoSalvo;
    }

    @Override
    public TipoPagamento buscarPorId(Long id) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        return repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Tipo de pagamento não encontrado ou não pertence à sua empresa"));
    }

    @Override
    public List<TipoPagamento> listarTodos() {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        List<TipoPagamento> tiposPagamento = repository.findByEmpresaId(empresaId);
        if (tiposPagamento.isEmpty()) {
            logger.info("Nenhum tipo de pagamento cadastrado na empresa " + empresaId);
        } else {
            logger.info(tiposPagamento.size() + " tipos de pagamento encontrados na empresa " + empresaId);
        }
        return tiposPagamento;
    }

    @Override
    @SuppressWarnings("null")
    public TipoPagamento atualizar(Long id, TipoPagamento novoTipoPagamento) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        TipoPagamento tipoPagamentoExistente = repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Tipo de pagamento não encontrado ou não pertence à sua empresa"));
        
        if (repository.existsByNomeAndIdNotAndEmpresaId(novoTipoPagamento.getNome(), id, empresaId)) {
            throw new NomeDuplicadoException("Nome de tipo de pagamento já cadastrado nesta empresa");
        }
        
        BeanUtils.copyProperties(novoTipoPagamento, tipoPagamentoExistente, "id", "empresa", "codigo", "createdAt", "updatedAt");
        return repository.save(tipoPagamentoExistente);
    }

    @Override
    @SuppressWarnings("null")
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
    
    @Override
    public Page<tecstock_spring.dto.TipoPagamentoPesquisaDTO> buscarPaginado(String query, Pageable pageable) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        if (query == null || query.trim().isEmpty()) {
            return repository.findByEmpresaId(empresaId, pageable);
        }
        
        return repository.searchByQueryAndEmpresaId(query.trim(), empresaId, pageable);
    }
    
    @Override
    public List<tecstock_spring.dto.TipoPagamentoPesquisaDTO> listarUltimosParaInicio(int limit) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        Pageable pageable = PageRequest.of(0, limit);
        return repository.findTopByEmpresaIdOrderByCreatedAtDesc(empresaId, pageable);
    }
}
