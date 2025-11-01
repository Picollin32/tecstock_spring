package tecstock_spring.service;

import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tecstock_spring.controller.ChecklistController;
import tecstock_spring.model.Checklist;
import tecstock_spring.repository.ChecklistRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChecklistServiceImpl implements ChecklistService {

    private final ChecklistRepository repository;
    Logger logger = Logger.getLogger(ChecklistController.class);

    @Override
    public Checklist salvar(Checklist checklist) {
        if (checklist.getId() == null && checklist.getNumeroChecklist() == 0) {
            Integer max = repository.findAll().stream()
                .mapToInt(Checklist::getNumeroChecklist)
                .max()
                .orElse(0);
            checklist.setNumeroChecklist(max + 1);
            logger.info("Gerando novo numeroChecklist: " + checklist.getNumeroChecklist());
        }
        Checklist checklistSalva = repository.save(checklist);
        logger.info("Checklist salva com sucesso: " + checklistSalva);
        return checklistSalva;
    }

    @Override
    public Checklist buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Checklist não encontrado"));
    }

    @Override
    public List<Checklist> listarTodos() {
        List<Checklist> checklists = repository.findAll();

        if (checklists != null && !checklists.isEmpty()) {
            checklists.sort((a, b) -> Integer.compare(a.getNumeroChecklist(), b.getNumeroChecklist()));
            logger.info(checklists.size() + " checklists encontrados (ordenados por numeroChecklist crescente).");
            System.out.println(checklists.size() + " checklists encontrados (ordenados por numeroChecklist crescente).");
        } else {
            logger.info("Nenhuma checklist cadastrada");
            System.out.println("Nenhuma checklist cadastrada");
        }
        return checklists;
    }

    @Override
    public Checklist atualizar(Long id, Checklist novoChecklist) {
        Checklist checklistExistente = buscarPorId(id);
        BeanUtils.copyProperties(novoChecklist, checklistExistente, "id", "numeroChecklist", "createdAt", "updatedAt");
        logger.info("Atualizando checklist ID: " + id + " - Preservando numeroChecklist: " + checklistExistente.getNumeroChecklist());
        return repository.save(checklistExistente);
    }

    @Override
    public void deletar(Long id) {
        repository.deleteById(id);
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
}
