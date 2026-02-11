package tecstock_spring.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import tecstock_spring.dto.ServicoOrdemServicoDTO;
import tecstock_spring.model.ServicoOrdemServico;
import tecstock_spring.service.ServicoOrdemServicoService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServicoOrdemServicoController {

    private final ServicoOrdemServicoService service;
    private static final Logger logger = LoggerFactory.getLogger(ServicoOrdemServicoController.class);

    @GetMapping("/api/servicos-ordem-servico/listarTodos")
    public List<ServicoOrdemServico> listarTodos() {
        logger.info("Listando todos os serviços realizados em ordens de serviço");
        return service.listarTodos();
    }

    @GetMapping("/api/servicos-ordem-servico/os/{numeroOS}")
    public List<ServicoOrdemServico> buscarPorOS(@PathVariable String numeroOS) {
        logger.info("Buscando serviços realizados na OS: " + numeroOS);
        return service.buscarPorNumeroOS(numeroOS);
    }

    @GetMapping("/api/servicos-ordem-servico/servico/{servicoId}")
    public List<ServicoOrdemServico> buscarPorServico(@PathVariable Long servicoId) {
        logger.info("Buscando histórico do serviço ID: " + servicoId);
        return service.buscarPorServicoId(servicoId);
    }

    @PostMapping("/api/servicos-ordem-servico/salvar")
    public ServicoOrdemServico salvar(@RequestBody ServicoOrdemServico servicoOrdemServico) {
        logger.info("Salvando registro de serviço realizado");
        return service.salvar(servicoOrdemServico);
    }

    @DeleteMapping("/api/servicos-ordem-servico/deletar/{id}")
    public void deletar(@PathVariable Long id) {
        logger.info("Deletando registro de serviço realizado ID: " + id);
        service.deletar(id);
    }

    @GetMapping("/api/servicos-ordem-servico/os/{numeroOS}/resumo")
    public List<ServicoOrdemServicoDTO> buscarResumosPorOS(@PathVariable String numeroOS) {
        logger.info("Buscando resumo dos serviços realizados na OS: " + numeroOS);
        List<ServicoOrdemServico> servicos = service.buscarPorNumeroOS(numeroOS);
        return servicos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/servicos-ordem-servico/resumo")
    public List<ServicoOrdemServicoDTO> listarResumos() {
        logger.info("Listando resumo de todos os serviços realizados");
        List<ServicoOrdemServico> servicos = service.listarTodos();
        return servicos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    private ServicoOrdemServicoDTO converterParaDTO(ServicoOrdemServico servicoOS) {
        return ServicoOrdemServicoDTO.builder()
                .id(servicoOS.getId())
                .numeroOS(servicoOS.getNumeroOS())
                .servicoNome(servicoOS.getServico() != null ? servicoOS.getServico().getNome() : null)
                .servicoId(servicoOS.getServico() != null ? servicoOS.getServico().getId() : null)
                .valor(servicoOS.getValor())
                .categoriaVeiculo(servicoOS.getCategoriaVeiculo())
                .dataRealizacao(servicoOS.getDataRealizacao())
                .observacoes(servicoOS.getObservacoes())
                .build();
    }
}
