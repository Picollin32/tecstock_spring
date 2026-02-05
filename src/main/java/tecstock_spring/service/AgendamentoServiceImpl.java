package tecstock_spring.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import tecstock_spring.controller.AgendamentoController;
import tecstock_spring.model.Agendamento;
import tecstock_spring.model.Empresa;
import tecstock_spring.repository.AgendamentoRepository;
import tecstock_spring.repository.EmpresaRepository;
import tecstock_spring.util.TenantContext;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoServiceImpl implements AgendamentoService {

    private final AgendamentoRepository repository;
    private final EmpresaRepository empresaRepository;
    Logger logger = Logger.getLogger(AgendamentoController.class);

    @Override
    public Agendamento salvar(Agendamento agendamento) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        Empresa empresa = empresaRepository.findById(empresaId)
            .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        agendamento.setEmpresa(empresa);
        
        Agendamento agendamentoSalvo = repository.save(agendamento);
        logger.info("Agendamento salvo com sucesso na empresa " + empresaId + ": " + agendamentoSalvo);
        return agendamentoSalvo;
    }

    @Override
    public List<Agendamento> listarTodos() {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        List<Agendamento> agendamentos = repository.findByEmpresaId(empresaId);
        if (agendamentos.isEmpty()) {
            logger.info("Nenhum agendamento cadastrado na empresa " + empresaId);
        } else {
            logger.info(agendamentos.size() + " agendamentos encontrados na empresa " + empresaId);
        }
        return agendamentos;
    }

    @Override
    public Agendamento atualizar(Long id, Agendamento agendamento) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        Agendamento existente = repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado ou não pertence à sua empresa: " + id));
        
        logger.info("=== DEBUG ATUALIZAÇÃO AGENDAMENTO ===");
        logger.info("Agendamento existente - horaInicio: " + existente.getHoraInicio() + ", horaFim: " + existente.getHoraFim());
        logger.info("Agendamento recebido - horaInicio: " + agendamento.getHoraInicio() + ", horaFim: " + agendamento.getHoraFim());
        
        existente.setData(agendamento.getData());
        existente.setHoraInicio(agendamento.getHoraInicio());
        existente.setHoraFim(agendamento.getHoraFim());
        existente.setPlacaVeiculo(agendamento.getPlacaVeiculo());
        existente.setNomeMecanico(agendamento.getNomeMecanico());
        existente.setNomeConsultor(agendamento.getNomeConsultor());
        existente.setCor(agendamento.getCor());

        logger.info("Agendamento antes de salvar - horaInicio: " + existente.getHoraInicio() + ", horaFim: " + existente.getHoraFim());

        Agendamento atualizado = repository.save(existente);
        logger.info("Agendamento atualizado com sucesso: " + atualizado);
        logger.info("Agendamento salvo - horaInicio: " + atualizado.getHoraInicio() + ", horaFim: " + atualizado.getHoraFim());
        logger.info("=====================================");
        return atualizado;
    }

    @Override
    public void deletar(Long id) {
        Long empresaId = TenantContext.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("Empresa não encontrada no contexto do usuário");
        }
        
        repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado ou não pertence à sua empresa"));
        
        repository.deleteById(id);
        logger.info("Agendamento excluído com sucesso da empresa " + empresaId);
    }
}