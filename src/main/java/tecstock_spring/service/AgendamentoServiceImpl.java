package tecstock_spring.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import tecstock_spring.controller.AgendamentoController;
import tecstock_spring.model.Agendamento;
import tecstock_spring.repository.AgendamentoRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AgendamentoServiceImpl implements AgendamentoService {

    private final AgendamentoRepository repository;
    Logger logger = Logger.getLogger(AgendamentoController.class);

    @Override
    public Agendamento salvar(Agendamento agendamento) {
        Agendamento agendamentoSalvo = repository.save(agendamento);
        if (agendamentoSalvo != null) {
            logger.info("Agendamento salvo com sucesso: " + agendamentoSalvo);
        } else {
            logger.error("Erro ao salvar agendamento: " + agendamento);
        }
        return agendamentoSalvo;
    }

    @Override
    public List<Agendamento> listarTodos() {
        List<Agendamento> agendamentos = repository.findAll();
        if (agendamentos.isEmpty()) {
            logger.info("Nenhum agendamento cadastrado.");
        } else {
            logger.info(agendamentos.size() + " agendamentos encontrados.");
        }
        return agendamentos;
    }

    @Override
    public Agendamento atualizar(Long id, Agendamento agendamento) {
        Optional<Agendamento> opt = repository.findById(id);
        if (opt.isEmpty()) {
            logger.error("Tentativa de atualizar agendamento não encontrado: " + id);
            throw new RuntimeException("Agendamento não encontrado: " + id);
        }
        Agendamento existente = opt.get();
        existente.setData(agendamento.getData());
        existente.setHoraInicio(agendamento.getHoraInicio());
        existente.setHoraFim(agendamento.getHoraFim());
        existente.setPlacaVeiculo(agendamento.getPlacaVeiculo());
        existente.setNomeMecanico(agendamento.getNomeMecanico());
        existente.setCor(agendamento.getCor());

        Agendamento atualizado = repository.save(existente);
        logger.info("Agendamento atualizado com sucesso: " + atualizado);
        return atualizado;
    }

    @Override
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}