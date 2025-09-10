package tecstock_spring.controller;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import tecstock_spring.dto.OrdemServicoResumoDTO;
import tecstock_spring.model.OrdemServico;
import tecstock_spring.service.OrdemServicoService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrdemServicoController {

    private final OrdemServicoService service;
    private static final Logger logger = Logger.getLogger(OrdemServicoController.class);

    @PostMapping("/api/ordens-servico/salvar")
    public OrdemServico salvar(@RequestBody OrdemServico ordemServico) {
        logger.info("Salvando ordem de serviço: " + ordemServico.getNumeroOS() + " no controller.");
        return service.salvar(ordemServico);
    }

    @GetMapping("/api/ordens-servico/buscar/{id}")
    public OrdemServico buscarPorId(@PathVariable Long id) {
        logger.info("Buscando ordem de serviço por ID: " + id);
        return service.buscarPorId(id);
    }
    
    @GetMapping("/api/ordens-servico/buscar-numero/{numeroOS}")
    public OrdemServico buscarPorNumeroOS(@PathVariable String numeroOS) {
        logger.info("Buscando ordem de serviço por número: " + numeroOS);
        return service.buscarPorNumeroOS(numeroOS);
    }

    @GetMapping("/api/ordens-servico/listarTodos")
    public List<OrdemServico> listarTodos() {
        logger.info("Listando todas as ordens de serviço no controller.");
        return service.listarTodos();
    }
    
    @GetMapping("/api/ordens-servico/cliente/{cpf}")
    public List<OrdemServico> listarPorCliente(@PathVariable String cpf) {
        logger.info("Listando ordens de serviço para cliente CPF: " + cpf);
        return service.listarPorCliente(cpf);
    }
    
    @GetMapping("/api/ordens-servico/veiculo/{placa}")
    public List<OrdemServico> listarPorVeiculo(@PathVariable String placa) {
        logger.info("Listando ordens de serviço para veículo placa: " + placa);
        return service.listarPorVeiculo(placa);
    }
    
    @GetMapping("/api/ordens-servico/status/{status}")
    public List<OrdemServico> listarPorStatus(@PathVariable String status) {
        logger.info("Listando ordens de serviço com status: " + status);
        return service.listarPorStatus(status);
    }
    
    @GetMapping("/api/ordens-servico/checklist/{checklistId}")
    public List<OrdemServico> listarPorChecklist(@PathVariable Long checklistId) {
        logger.info("Listando ordens de serviço para checklist ID: " + checklistId);
        return service.listarPorChecklist(checklistId);
    }
    
    @GetMapping("/api/ordens-servico/periodo")
    public List<OrdemServico> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        logger.info("Listando ordens de serviço no período: " + inicio + " até " + fim);
        return service.listarPorPeriodo(inicio, fim);
    }

    @PutMapping("/api/ordens-servico/atualizar/{id}")
    public OrdemServico atualizar(@PathVariable Long id, @RequestBody OrdemServico ordemServico) {
        logger.info("Atualizando ordem de serviço no controller. ID: " + id + ", OS: " + ordemServico.getNumeroOS());
        return service.atualizar(id, ordemServico);
    }

    @DeleteMapping("/api/ordens-servico/deletar/{id}")
    public void deletar(@PathVariable Long id) {
        logger.info("Deletando ordem de serviço no controller. ID: " + id);
        service.deletar(id);
    }
    
    @PatchMapping("/api/ordens-servico/{id}/status")
    public OrdemServico atualizarStatus(@PathVariable Long id, @RequestParam String status) {
        logger.info("Atualizando status da OS ID: " + id + " para: " + status);
        OrdemServico ordemServico = service.buscarPorId(id);
        ordemServico.setStatus(status);
        return service.atualizar(id, ordemServico);
    }
    
    @GetMapping("/api/ordens-servico/resumo")
    public List<OrdemServicoResumoDTO> listarResumos() {
        logger.info("Listando resumos das ordens de serviço.");
        List<OrdemServico> ordensServico = service.listarTodos();
        
        return ordensServico.stream()
                .map(this::converterParaResumo)
                .collect(Collectors.toList());
    }
    
    private OrdemServicoResumoDTO converterParaResumo(OrdemServico os) {
        return OrdemServicoResumoDTO.builder()
                .id(os.getId())
                .numeroOS(os.getNumeroOS())
                .dataHora(os.getDataHora())
                .clienteNome(os.getClienteNome())
                .clienteCpf(os.getClienteCpf())
                .veiculoNome(os.getVeiculoNome())
                .veiculoPlaca(os.getVeiculoPlaca())
                .precoTotal(os.getPrecoTotal())
                .status(os.getStatus())
                .quantidadeServicos(os.getServicosRealizados() != null ? os.getServicosRealizados().size() : 0)
                .tipoPagamento(os.getTipoPagamento() != null ? os.getTipoPagamento().getNome() : null)
                .garantiaMeses(os.getGarantiaMeses())
                .createdAt(os.getCreatedAt())
                .observacoes(os.getObservacoes())
                .build();
    }
}
