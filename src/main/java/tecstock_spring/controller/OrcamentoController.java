package tecstock_spring.controller;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import tecstock_spring.model.Orcamento;
import tecstock_spring.service.OrcamentoService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrcamentoController {

    private final OrcamentoService service;
    private static final Logger logger = Logger.getLogger(OrcamentoController.class);

    @PostMapping("/api/orcamentos/salvar")
    public Orcamento salvar(@RequestBody Orcamento orcamento) {
        logger.info("Salvando orçamento: " + orcamento.getNumeroOrcamento() + " no controller.");
        
        // Validar descontos antes de salvar
        if (!orcamento.isDescontoServicosValido(orcamento.getDescontoServicos())) {
            throw new IllegalArgumentException("Desconto de serviços excede o limite máximo de 10%");
        }
        
        if (!orcamento.isDescontoPecasValido(orcamento.getDescontoPecas())) {
            throw new IllegalArgumentException("Desconto de peças excede a margem de lucro disponível");
        }
        
        return service.salvar(orcamento);
    }

    @GetMapping("/api/orcamentos/buscar/{id}")
    public Orcamento buscarPorId(@PathVariable Long id) {
        logger.info("Buscando orçamento por ID: " + id);
        return service.buscarPorId(id);
    }
    
    @GetMapping("/api/orcamentos/buscar-numero/{numeroOrcamento}")
    public Orcamento buscarPorNumeroOrcamento(@PathVariable String numeroOrcamento) {
        logger.info("Buscando orçamento por número: " + numeroOrcamento);
        return service.buscarPorNumeroOrcamento(numeroOrcamento);
    }

    @GetMapping("/api/orcamentos/listarTodos")
    public List<Orcamento> listarTodos() {
        logger.info("Listando todos os orçamentos no controller.");
        return service.listarTodos();
    }
    
    @GetMapping("/api/orcamentos/cliente/{cpf}")
    public List<Orcamento> listarPorCliente(@PathVariable String cpf) {
        logger.info("Listando orçamentos para cliente CPF: " + cpf);
        return service.listarPorCliente(cpf);
    }
    
    @GetMapping("/api/orcamentos/veiculo/{placa}")
    public List<Orcamento> listarPorVeiculo(@PathVariable String placa) {
        logger.info("Listando orçamentos para veículo placa: " + placa);
        return service.listarPorVeiculo(placa);
    }
    
    @GetMapping("/api/orcamentos/status/{status}")
    public List<Orcamento> listarPorStatus(@PathVariable String status) {
        logger.info("Listando orçamentos com status: " + status);
        return service.listarPorStatus(status);
    }
    
    @GetMapping("/api/orcamentos/periodo")
    public List<Orcamento> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        logger.info("Listando orçamentos no período: " + inicio + " até " + fim);
        return service.listarPorPeriodo(inicio, fim);
    }

    @PutMapping("/api/orcamentos/atualizar/{id}")
    public Orcamento atualizar(@PathVariable Long id, @RequestBody Orcamento orcamento) {
        logger.info("Atualizando orçamento no controller. ID: " + id + ", Orçamento: " + orcamento.getNumeroOrcamento());
        
        // Validar descontos antes de atualizar
        if (!orcamento.isDescontoServicosValido(orcamento.getDescontoServicos())) {
            throw new IllegalArgumentException("Desconto de serviços excede o limite máximo de 10%");
        }
        
        if (!orcamento.isDescontoPecasValido(orcamento.getDescontoPecas())) {
            throw new IllegalArgumentException("Desconto de peças excede a margem de lucro disponível");
        }
        
        return service.atualizar(id, orcamento);
    }

    @DeleteMapping("/api/orcamentos/deletar/{id}")
    public void deletar(@PathVariable Long id) {
        logger.info("Deletando orçamento no controller. ID: " + id);
        service.deletar(id);
    }
    
    @PatchMapping("/api/orcamentos/{id}/status")
    public Orcamento atualizarStatus(@PathVariable Long id, @RequestParam String status) {
        logger.info("Atualizando status do orçamento ID: " + id + " para: " + status);
        Orcamento orcamento = service.buscarPorId(id);
        orcamento.setStatus(status);
        return service.atualizar(id, orcamento);
    }
    
    @PatchMapping("/api/orcamentos/{id}/recalcular-precos")
    public Orcamento recalcularPrecos(@PathVariable Long id) {
        logger.info("Recalculando preços do orçamento ID: " + id);
        Orcamento orcamento = service.buscarPorId(id);
        orcamento.calcularTodosOsPrecos();
        return service.atualizar(id, orcamento);
    }
    
    @GetMapping("/api/orcamentos/{id}/max-descontos")
    public java.util.Map<String, Double> calcularMaxDescontos(@PathVariable Long id) {
        logger.info("Calculando máximos de desconto para orçamento ID: " + id);
        Orcamento orcamento = service.buscarPorId(id);
        
        java.util.Map<String, Double> maxDescontos = new java.util.HashMap<>();
        maxDescontos.put("maxDescontoServicos", orcamento.calcularMaxDescontoServicos());
        maxDescontos.put("maxDescontoPecas", orcamento.calcularMaxDescontoPecas());
        
        return maxDescontos;
    }
}