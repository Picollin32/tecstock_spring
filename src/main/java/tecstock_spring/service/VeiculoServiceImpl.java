package tecstock_spring.service;

import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tecstock_spring.controller.VeiculoController;
import tecstock_spring.exception.PlacaDuplicadaException;
import tecstock_spring.exception.VeiculoEmUsoException;
import tecstock_spring.model.Empresa;
import tecstock_spring.model.Veiculo;
import tecstock_spring.repository.EmpresaRepository;
import tecstock_spring.repository.VeiculoRepository;
import tecstock_spring.repository.MarcaRepository;
import tecstock_spring.util.TenantContext;
import tecstock_spring.repository.ChecklistRepository;
import tecstock_spring.repository.AgendamentoRepository;
import tecstock_spring.repository.OrdemServicoRepository;
import tecstock_spring.model.Marca;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VeiculoServiceImpl implements VeiculoService {

    private final VeiculoRepository repository;
    private final EmpresaRepository empresaRepository;
    private final MarcaRepository marcaRepository;
    private final ChecklistRepository checklistRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    Logger logger = LoggerFactory.getLogger(VeiculoController.class);

    @Override
    public Veiculo salvar(Veiculo veiculo) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        if (repository.existsByPlacaAndEmpresaId(veiculo.getPlaca(), empresaId)) {
            throw new PlacaDuplicadaException("Placa já cadastrada nesta empresa");
        }
        
        if (veiculo.getMarca() == null || veiculo.getMarca().getId() == null) {
            throw new IllegalArgumentException("A marca do veículo é obrigatória.");
        }
        
        Long marcaId = Objects.requireNonNull(veiculo.getMarca().getId(), "ID da marca não pode ser nulo");
        Marca marcaGerenciada = marcaRepository.findById(marcaId)
            .orElseThrow(() -> new RuntimeException("Marca com ID " + marcaId + " não encontrada."));
        veiculo.setMarca(marcaGerenciada);
        
        Empresa empresa = empresaRepository.findById(empresaId)
            .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        veiculo.setEmpresa(empresa);
        
        Veiculo veiculoSalvo = repository.save(veiculo);
        logger.info("Veículo salvo com sucesso na empresa " + empresaId + ": " + veiculoSalvo);
        return veiculoSalvo;
    }

    @Override
    public Veiculo buscarPorId(Long id) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        return repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado ou não pertence à sua empresa"));
    }

    @Override
    public List<Veiculo> listarTodos() {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        List<Veiculo> veiculos = repository.findByEmpresaId(empresaId);
        if (veiculos.isEmpty()) {
            logger.info("Nenhum veículo cadastrado na empresa " + empresaId);
        } else {
            logger.info(veiculos.size() + " veículos encontrados na empresa " + empresaId);
        }
        return veiculos;
    }

    @Override
    @SuppressWarnings("null")
    public Veiculo atualizar(Long id, Veiculo novoVeiculo) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        Veiculo veiculoExistente = repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado ou não pertence à sua empresa"));

        if (repository.existsByPlacaAndIdNotAndEmpresaId(novoVeiculo.getPlaca(), id, empresaId)) {
            throw new PlacaDuplicadaException("Placa já cadastrada nesta empresa");
        }
        
        if (novoVeiculo.getMarca() == null || novoVeiculo.getMarca().getId() == null) {
            throw new IllegalArgumentException("A marca do veículo é obrigatória.");
        }
        
        Long marcaId = Objects.requireNonNull(novoVeiculo.getMarca().getId(), "ID da marca não pode ser nulo");
        Marca marcaGerenciada = marcaRepository.findById(marcaId)
            .orElseThrow(() -> new RuntimeException("Marca com ID " + marcaId + " não encontrada."));
        novoVeiculo.setMarca(marcaGerenciada);

        BeanUtils.copyProperties(novoVeiculo, veiculoExistente, "id", "empresa", "createdAt", "updatedAt");
        return repository.save(veiculoExistente);
    }

    @Override
    @SuppressWarnings("null")
    public void deletar(Long id) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        Veiculo veiculo = repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado ou não pertence à sua empresa"));
        
        if (ordemServicoRepository != null && ordemServicoRepository.existsByVeiculoPlacaAndEmpresaId(veiculo.getPlaca(), empresaId)) {
            throw new VeiculoEmUsoException("Veículo não pode ser excluído: está em uso em uma Ordem de Serviço (OS) com placa " + veiculo.getPlaca());
        }

        if (checklistRepository.existsByVeiculoPlacaAndEmpresaId(veiculo.getPlaca(), empresaId)) {
            throw new VeiculoEmUsoException("Veículo não pode ser excluído: está em uso em um Checklist com placa " + veiculo.getPlaca());
        }

        if (agendamentoRepository.existsByPlacaVeiculoAndEmpresaId(veiculo.getPlaca(), empresaId)) {
            throw new VeiculoEmUsoException("Veículo não pode ser excluído: está em uso em um Agendamento com placa " + veiculo.getPlaca());
        }
        
        repository.deleteById(id);
        logger.info("Veículo excluído com sucesso da empresa " + empresaId + ": " + veiculo.getPlaca());
    }
    
    @Override
    public Page<tecstock_spring.dto.VeiculoPesquisaDTO> buscarPaginado(String query, Pageable pageable) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        if (query == null || query.trim().isEmpty()) {
            return repository.findByEmpresaId(empresaId, pageable);
        }
        
        return repository.searchByQueryAndEmpresaId(query.trim(), empresaId, pageable);
    }
}
