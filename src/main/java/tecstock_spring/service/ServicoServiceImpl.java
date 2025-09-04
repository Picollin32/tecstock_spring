package tecstock_spring.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tecstock_spring.controller.ServicoController;
import tecstock_spring.model.Servico;
import tecstock_spring.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicoServiceImpl implements ServicoService {

    private final ServicoRepository repository;
    Logger logger = Logger.getLogger(ServicoController.class);

    @Override
    public Servico salvar(Servico servico) {
        Servico servicoSalvo = repository.save(servico);
        if (servicoSalvo != null) {
            logger.info("Serviço salvo com sucesso: " + servicoSalvo);
        } else {
            logger.error("Erro ao salvar serviço: " + servico);
        }
        return servicoSalvo;
    }

    @Override
    public Servico buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
    }

    @Override
    public List<Servico> listarTodos() {
        List<Servico> servicos = repository.findAll();
        if (servicos.isEmpty()) {
            logger.info("Nenhum serviço cadastrado.");
        } else {
            logger.info(servicos.size() + " serviços encontrados.");
        }
        return servicos;
    }

    @Override
    public Servico atualizar(Long id, Servico novoServico) {
        Servico servicoExistente = buscarPorId(id);
        BeanUtils.copyProperties(novoServico, servicoExistente, "id");
        return repository.save(servicoExistente);
    }

    @Override
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}