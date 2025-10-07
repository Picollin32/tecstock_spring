package tecstock_spring.controller;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

        if (!ordemServico.isDescontoServicosValido(ordemServico.getDescontoServicos())) {
            throw new IllegalArgumentException("Desconto de serviços excede o limite máximo de 10%");
        }
        
        if (!ordemServico.isDescontoPecasValido(ordemServico.getDescontoPecas())) {
            throw new IllegalArgumentException("Desconto de peças excede a margem de lucro disponível");
        }
        
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
        
        if (!ordemServico.isDescontoServicosValido(ordemServico.getDescontoServicos())) {
            throw new IllegalArgumentException("Desconto de serviços excede o limite máximo de 10%");
        }
        
        if (!ordemServico.isDescontoPecasValido(ordemServico.getDescontoPecas())) {
            throw new IllegalArgumentException("Desconto de peças excede a margem de lucro disponível");
        }
        
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
        return service.atualizarApenasStatus(id, status);
    }
    
    @PostMapping("/api/ordens-servico/{id}/fechar")
    public OrdemServico fecharOrdemServico(@PathVariable Long id) {
        logger.info("🔥 CONTROLLER: Recebida solicitação para fechar OS com ID: " + id);
        OrdemServico resultado = service.fecharOrdemServico(id);
        logger.info("🎉 CONTROLLER: OS encerrada com sucesso - Número: " + resultado.getNumeroOS() + 
                   " | Status: " + resultado.getStatus());
        return resultado;
    }
    
    @PatchMapping("/api/ordens-servico/{id}/reabrir")
    public ResponseEntity<?> reabrirOrdemServico(@PathVariable Long id) {
        try {
            logger.info("🔓 CONTROLLER: Recebida solicitação para reabrir OS com ID: " + id);
            OrdemServico os = service.buscarPorId(id);
            
            if (!"Encerrada".equals(os.getStatus())) {
                logger.warn("⚠️ CONTROLLER: Tentativa de reabrir OS que não está encerrada. Status atual: " + os.getStatus());
                return ResponseEntity.badRequest()
                    .body("Apenas ordens de serviço encerradas podem ser reabertas. Status atual: " + os.getStatus());
            }

            logger.info("📊 Dados da OS antes da reabertura:");
            logger.info("  - Serviços: " + (os.getServicosRealizados() != null ? os.getServicosRealizados().size() : "null"));
            logger.info("  - Peças: " + (os.getPecasUtilizadas() != null ? os.getPecasUtilizadas().size() : "null"));

            OrdemServico osReaberta = service.reabrirOS(id);

            logger.info("📊 Dados da OS após a reabertura:");
            logger.info("  - Serviços: " + (osReaberta.getServicosRealizados() != null ? osReaberta.getServicosRealizados().size() : "null"));
            logger.info("  - Peças: " + (osReaberta.getPecasUtilizadas() != null ? osReaberta.getPecasUtilizadas().size() : "null"));
            
            logger.info("✅ CONTROLLER: OS reaberta com sucesso - Número: " + osReaberta.getNumeroOS());
            return ResponseEntity.ok(osReaberta);
        } catch (Exception e) {
            logger.error("❌ CONTROLLER: Erro ao reabrir ordem de serviço: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao reabrir ordem de serviço: " + e.getMessage());
        }
    }
    
    @PatchMapping("/api/ordens-servico/{id}/recalcular-precos")
    public OrdemServico recalcularPrecos(@PathVariable Long id) {
        logger.info("Recalculando preços da OS ID: " + id);
        OrdemServico ordemServico = service.buscarPorId(id);
        ordemServico.forcarRecalculoTodosOsPrecos();
        return service.atualizar(id, ordemServico);
    }
    
    @GetMapping("/api/ordens-servico/{id}/max-descontos")
    public java.util.Map<String, Double> calcularMaxDescontos(@PathVariable Long id) {
        logger.info("Calculando máximos de desconto para OS ID: " + id);
        OrdemServico ordemServico = service.buscarPorId(id);
        
        java.util.Map<String, Double> maxDescontos = new java.util.HashMap<>();
        maxDescontos.put("maxDescontoServicos", ordemServico.calcularMaxDescontoServicos());
        maxDescontos.put("maxDescontoPecas", ordemServico.calcularMaxDescontoPecas());
        
        return maxDescontos;
    }
    
    @GetMapping("/api/ordens-servico/resumo")
    public List<OrdemServicoResumoDTO> listarResumos() {
        logger.info("Listando resumos das ordens de serviço.");
        List<OrdemServico> ordensServico = service.listarTodos();
        
        return ordensServico.stream()
                .map(this::converterParaResumo)
                .collect(Collectors.toList());
    }
    
    @GetMapping("/api/ordens-servico/fiados-em-aberto")
    public List<OrdemServico> getFiadosEmAberto() {
        logger.info("Buscando fiados em aberto");
        return service.getFiadosEmAberto();
    }
    
    @PatchMapping("/api/ordens-servico/{id}/fiado-pago")
    public OrdemServico marcarFiadoComoPago(@PathVariable Long id, @RequestParam Boolean pago) {
        logger.info("Marcando fiado ID: " + id + " como " + (pago ? "PAGO" : "NÃO PAGO"));
        return service.marcarFiadoComoPago(id, pago);
    }
    
    @PostMapping("/api/ordens-servico/{id}/desbloquear")
    public ResponseEntity<?> desbloquearOS(@PathVariable Long id, @RequestHeader(value = "X-User-Level", required = false) Integer userLevel) {
        if (userLevel == null || userLevel != 0) {
            logger.warn("Acesso negado ao desbloquear OS. Nível: " + userLevel);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado. Apenas administradores podem desbloquear Ordens de Serviço.");
        }
        logger.info("Desbloqueando OS ID: " + id + " para edição");
        OrdemServico os = service.desbloquearParaEdicao(id);
        return ResponseEntity.ok(os);
    }
    
    private OrdemServicoResumoDTO converterParaResumo(OrdemServico os) {
        return OrdemServicoResumoDTO.builder()
                .id(os.getId())
                .numeroOS(os.getNumeroOS())
                .dataHora(os.getDataHora())
                .dataHoraEncerramento(os.getDataHoraEncerramento())
                .clienteNome(os.getClienteNome())
                .clienteCpf(os.getClienteCpf())
                .veiculoNome(os.getVeiculoNome())
                .veiculoPlaca(os.getVeiculoPlaca())
                .precoTotal(os.getPrecoTotal())
                .precoTotalServicos(os.getPrecoTotalServicos())
                .precoTotalPecas(os.getPrecoTotalPecas())
                .descontoServicos(os.getDescontoServicos())
                .descontoPecas(os.getDescontoPecas())
                .status(os.getStatus())
                .quantidadeServicos(os.getServicosRealizados() != null ? os.getServicosRealizados().size() : 0)
                .tipoPagamento(os.getTipoPagamento() != null ? os.getTipoPagamento().getNome() : null)
                .garantiaMeses(os.getGarantiaMeses())
                .nomeMecanico(os.getMecanico() != null ? os.getMecanico().getNome() : null)
                .nomeConsultor(os.getConsultor() != null ? os.getConsultor().getNome() : null)
                .numeroParcelas(os.getNumeroParcelas())
                .createdAt(os.getCreatedAt())
                .observacoes(os.getObservacoes())
                .build();
    }
}
