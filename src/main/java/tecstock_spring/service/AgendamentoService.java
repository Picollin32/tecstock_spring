package tecstock_spring.service;

import tecstock_spring.model.Agendamento;
import java.util.List;

public interface AgendamentoService {
    Agendamento salvar(Agendamento agendamento);

    List<Agendamento> listarTodos();

    Agendamento atualizar(Long id, Agendamento agendamento);

    void deletar(Long id);
}