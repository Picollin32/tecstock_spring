package tecstock_spring.service;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tecstock_spring.model.OrdemServico;
import tecstock_spring.model.Servico;
import tecstock_spring.model.ServicoOrdemServico;
import tecstock_spring.repository.ServicoOrdemServicoRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicoOrdemServicoServiceImpl implements ServicoOrdemServicoService {

    private final ServicoOrdemServicoRepository repository;
    private static final Logger logger = Logger.getLogger(ServicoOrdemServicoServiceImpl.class);

    @Override
    public ServicoOrdemServico salvar(ServicoOrdemServico servicoOrdemServico) {
        logger.info("Salvando registro de serviço realizado na OS: " + servicoOrdemServico.getNumeroOS());
        return repository.save(servicoOrdemServico);
    }

    @Override
    @Transactional
    public void registrarServicosRealizados(OrdemServico ordemServico) {
        if (ordemServico.getServicosRealizados() == null || ordemServico.getServicosRealizados().isEmpty()) {
            logger.info("Nenhum serviço para registrar na OS: " + ordemServico.getNumeroOS());
            return;
        }
        
        List<ServicoOrdemServico> servicosAntigos = repository.findByNumeroOSOrderByDataRealizacaoDesc(ordemServico.getNumeroOS());
        if (!servicosAntigos.isEmpty()) {
            logger.info("Removendo " + servicosAntigos.size() + " registros antigos de serviços da OS: " + ordemServico.getNumeroOS());
            repository.deleteAll(servicosAntigos);
            logger.info("Registros antigos removidos com sucesso");
        }

        logger.info("Registrando " + ordemServico.getServicosRealizados().size() + 
                   " serviços realizados na OS: " + ordemServico.getNumeroOS());

        int servicosRegistrados = 0;
        for (Servico servico : ordemServico.getServicosRealizados()) {
            try {
                Double valorServico = servico.precoParaCategoria(ordemServico.getVeiculoCategoria());
                
                ServicoOrdemServico servicoOS = ServicoOrdemServico.builder()
                    .servico(servico)
                    .numeroOS(ordemServico.getNumeroOS())
                    .valor(valorServico)
                    .categoriaVeiculo(ordemServico.getVeiculoCategoria())
                    .observacoes("Serviço realizado ao encerrar OS")
                    .build();

                repository.save(servicoOS);
                servicosRegistrados++;
                
                logger.info("Serviço '" + servico.getNome() + "' registrado com sucesso - Valor: R$ " + 
                           valorServico + " | Categoria: " + ordemServico.getVeiculoCategoria());
                
            } catch (Exception e) {
                logger.error("Erro ao registrar serviço '" + servico.getNome() + 
                           "' para OS " + ordemServico.getNumeroOS() + ": " + e.getMessage());
                logger.error("Stack trace:", e);
            }
        }

        logger.info("Registro de serviços concluído: " + servicosRegistrados + "/" + 
                   ordemServico.getServicosRealizados().size() + 
                   " serviços registrados para OS " + ordemServico.getNumeroOS());
    }

    @Override
    public List<ServicoOrdemServico> buscarPorNumeroOS(String numeroOS) {
        logger.info("Buscando serviços realizados na OS: " + numeroOS);
        return repository.findByNumeroOSOrderByDataRealizacaoDesc(numeroOS);
    }

    @Override
    public List<ServicoOrdemServico> listarTodos() {
        logger.info("Listando todos os serviços realizados");
        return repository.findAllByOrderByDataRealizacaoDesc();
    }

    @Override
    public List<ServicoOrdemServico> buscarPorServicoId(Long servicoId) {
        logger.info("Buscando registros do serviço ID: " + servicoId);
        return repository.findByServicoIdOrderByDataRealizacaoDesc(servicoId);
    }

    @Override
    public void deletar(Long id) {
        logger.info("Deletando registro de serviço realizado ID: " + id);
        repository.deleteById(id);
    }
}
