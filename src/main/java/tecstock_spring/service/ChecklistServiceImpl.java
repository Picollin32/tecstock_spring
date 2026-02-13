package tecstock_spring.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tecstock_spring.controller.ChecklistController;
import tecstock_spring.model.Checklist;
import tecstock_spring.model.Empresa;
import tecstock_spring.repository.ChecklistRepository;
import tecstock_spring.repository.EmpresaRepository;
import tecstock_spring.util.TenantContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ChecklistServiceImpl implements ChecklistService {

    private final ChecklistRepository repository;
    private final EmpresaRepository empresaRepository;
    Logger logger = LoggerFactory.getLogger(ChecklistController.class);

    @Override
    public Checklist salvar(Checklist checklist) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        Empresa empresa = empresaRepository.findById(empresaId)
            .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        checklist.setEmpresa(empresa);
        
        if (checklist.getId() == null && checklist.getNumeroChecklist() == 0) {
            Integer max = repository.findByEmpresaId(empresaId).stream()
                .mapToInt(Checklist::getNumeroChecklist)
                .max()
                .orElse(0);
            checklist.setNumeroChecklist(max + 1);
            logger.info("Gerando novo numeroChecklist: " + checklist.getNumeroChecklist() + " para empresa " + empresaId);
        }
        Checklist checklistSalva = repository.save(checklist);
        logger.info("Checklist salva com sucesso na empresa " + empresaId + ": " + checklistSalva);
        return checklistSalva;
    }

    @Override
    public Checklist buscarPorId(Long id) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        return repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Checklist não encontrado ou não pertence à sua empresa"));
    }

    @Override
    public List<Checklist> listarTodos() {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        List<Checklist> checklists = repository.findByEmpresaId(empresaId);

        if (checklists != null && !checklists.isEmpty()) {
            checklists.sort((a, b) -> Integer.compare(a.getNumeroChecklist(), b.getNumeroChecklist()));
            logger.info(checklists.size() + " checklists encontrados na empresa " + empresaId + " (ordenados por numeroChecklist crescente).");
            System.out.println(checklists.size() + " checklists encontrados na empresa " + empresaId + " (ordenados por numeroChecklist crescente).");
        } else {
            logger.info("Nenhuma checklist cadastrada na empresa " + empresaId);
            System.out.println("Nenhuma checklist cadastrada na empresa " + empresaId);
        }
        return checklists;
    }

    @Override
    public Checklist atualizar(Long id, Checklist novoChecklist) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        Checklist checklistExistente = repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Checklist não encontrado ou não pertence à sua empresa"));
        
        BeanUtils.copyProperties(novoChecklist, checklistExistente, "id", "empresa", "numeroChecklist", "createdAt", "updatedAt");
        logger.info("Atualizando checklist ID: " + id + " da empresa " + empresaId + " - Preservando numeroChecklist: " + checklistExistente.getNumeroChecklist());
        return repository.save(checklistExistente);
    }

    @Override
    public void deletar(Long id) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Checklist não encontrado ou não pertence à sua empresa"));
        
        repository.deleteById(id);
        logger.info("Checklist excluído com sucesso da empresa " + empresaId);
    }
    
    @Override
    public boolean fecharChecklist(Long id) {
        try {
            Checklist checklist = buscarPorId(id);
            checklist.setStatus("Fechado");
            repository.save(checklist);
            logger.info("Checklist ID " + id + " foi fechado com sucesso");
            return true;
        } catch (Exception e) {
            logger.error("Erro ao fechar checklist ID " + id + ": " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean reabrirChecklist(Long id) {
        try {
            Checklist checklist = buscarPorId(id);
            checklist.setStatus("Aberto");
            repository.save(checklist);
            logger.info("Checklist ID " + id + " foi reaberto com sucesso");
            return true;
        } catch (Exception e) {
            logger.error("Erro ao reabrir checklist ID " + id + ": " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public List<Checklist> pesquisarPorNumeroExato(Integer numero) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        try {
            List<Checklist> checklists = repository.findByEmpresaId(empresaId);
            List<Checklist> resultado = checklists.stream()
                    .filter(c -> c.getNumeroChecklist() == numero)
                    .collect(java.util.stream.Collectors.toList());
            
            if (!resultado.isEmpty()) {
                logger.info("Checklist encontrado com número exato: " + numero);
            } else {
                logger.info("Nenhum checklist encontrado com número: " + numero);
            }
            return resultado;
        } catch (Exception e) {
            logger.error("Erro ao pesquisar checklist por número " + numero + ": " + e.getMessage());
            return List.of();
        }
    }
    
    @Override
    public Page<Checklist> buscarPaginado(String query, String tipo, Pageable pageable) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        if (query == null || query.trim().isEmpty()) {
            return repository.findByEmpresaIdOrderByCreatedAtDesc(empresaId, pageable);
        }

        String tipoBusca = tipo == null ? "numero" : tipo.trim().toLowerCase();
        String termo = query.trim();
        if ("placa".equals(tipoBusca)) {
            return repository.searchByVeiculoPlacaAndEmpresaId(termo, empresaId, pageable);
        }

        return repository.searchByNumeroChecklistAndEmpresaId(termo, empresaId, pageable);
    }
}
