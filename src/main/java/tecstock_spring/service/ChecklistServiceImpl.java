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
        if (checklists != null && checklists.isEmpty()) {
            logger.info("Nenhuma checklist cadastrada: " + checklists);
            System.out.println("Nenhuma checklist cadastrada: " + checklists);
        } else if (checklists != null && !checklists.isEmpty()) {
            logger.info(checklists.size() + " checklists encontrados.");
            System.out.println(checklists.size() + " checklists encontrados.");
        }
        return checklists;
    }

    @Override
    public Checklist atualizar(Long id, Checklist novoChecklist) {
        Checklist checklistExistente = buscarPorId(id);
        BeanUtils.copyProperties(novoChecklist, checklistExistente, "id", "numeroChecklist");
        logger.info("Atualizando checklist ID: " + id + " - Preservando numeroChecklist: " + checklistExistente.getNumeroChecklist());
        return repository.save(checklistExistente);
    }

    @Override
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
