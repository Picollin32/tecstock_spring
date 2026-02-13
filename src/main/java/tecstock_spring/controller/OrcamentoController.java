package tecstock_spring.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import tecstock_spring.dto.OrcamentoPesquisaDTO;
import tecstock_spring.model.Orcamento;
import tecstock_spring.model.OrdemServico;
import tecstock_spring.service.OrcamentoService;
import tecstock_spring.util.TenantContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrcamentoController {

    private final OrcamentoService service;
    private static final Logger logger = LoggerFactory.getLogger(OrcamentoController.class);

    @PostMapping("/api/orcamentos/salvar")
    public Orcamento salvar(@RequestBody Orcamento orcamento) {
        logger.info("Salvando orçamento: " + orcamento.getNumeroOrcamento() + " no controller.");

        if (!TenantContext.isAdmin()) {
            if (!orcamento.isDescontoServicosValido(orcamento.getDescontoServicos())) {
                throw new IllegalArgumentException("Desconto de serviços excede o limite máximo de 10%");
            }
            
            if (!orcamento.isDescontoPecasValido(orcamento.getDescontoPecas())) {
                throw new IllegalArgumentException("Desconto de peças excede a margem de lucro disponível");
            }
        } else {
            logger.info("Validação de desconto ignorada - Usuário é admin (nivelAcesso: " + TenantContext.getCurrentNivelAcesso() + ")");
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
    
    @GetMapping("/api/orcamentos/pesquisar")
    public List<OrcamentoPesquisaDTO> pesquisarPorNumeroExato(@RequestParam String numero) {
        logger.info("Pesquisando orçamento com número exato: " + numero);
        List<Orcamento> orcamentos = service.pesquisarPorNumeroExato(numero);
        return orcamentos.stream()
                .map(this::converterParaPesquisaDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/api/orcamentos/listarTodos")
    public List<Orcamento> listarTodos() {
        logger.info("Listando todos os orçamentos no controller.");
        return service.listarTodos();
    }

    @GetMapping("/api/orcamentos/buscarPaginado")
    public Object buscarPaginado(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false, defaultValue = "numero") String tipo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // Se não há pesquisa, retorna os últimos 5 sem paginação
        if (query == null || query.trim().isEmpty()) {
            List<Orcamento> lista = service.listarUltimosParaInicio(5);
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("content", lista);
            response.put("totalElements", lista.size());
            response.put("totalPages", 1);
            response.put("number", 0);
            return response;
        }
        // Com pesquisa, usa paginação com 10 elementos
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return service.buscarPaginado(query, tipo, pageable);
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

        if (!TenantContext.isAdmin()) {
            if (!orcamento.isDescontoServicosValido(orcamento.getDescontoServicos())) {
                throw new IllegalArgumentException("Desconto de serviços excede o limite máximo de 10%");
            }
            
            if (!orcamento.isDescontoPecasValido(orcamento.getDescontoPecas())) {
                throw new IllegalArgumentException("Desconto de peças excede a margem de lucro disponível");
            }
        } else {
            logger.info("Validação de desconto ignorada - Usuário é admin (nivelAcesso: " + TenantContext.getCurrentNivelAcesso() + ")");
        }
        
        return service.atualizar(id, orcamento);
    }

    @DeleteMapping("/api/orcamentos/deletar/{id}")
    public void deletar(@PathVariable Long id) {
        logger.info("Deletando orçamento no controller. ID: " + id);
        service.deletar(id);
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
    
    @PostMapping("/api/orcamentos/{id}/transformar-em-os")
    public OrdemServico transformarEmOrdemServico(@PathVariable Long id) {
        logger.info("Transformando orçamento ID: " + id + " em Ordem de Serviço");
        try {
            OrdemServico os = service.transformarEmOrdemServico(id);
            logger.info("Orçamento transformado com sucesso. OS criada: " + os.getNumeroOS());
            return os;
        } catch (Exception e) {
            logger.error("ERRO ao transformar orçamento ID " + id + ": " + e.getMessage(), e);
            throw e;
        }
    }
    
    private OrcamentoPesquisaDTO converterParaPesquisaDTO(Orcamento orc) {
        return OrcamentoPesquisaDTO.builder()
                .id(orc.getId())
                .numeroOrcamento(orc.getNumeroOrcamento())
                .dataHora(orc.getDataHora())
                .clienteNome(orc.getClienteNome())
                .clienteCpf(orc.getClienteCpf())
                .veiculoNome(orc.getVeiculoNome())
                .veiculoPlaca(orc.getVeiculoPlaca())
                .precoTotal(orc.getPrecoTotal())
                .tipoPagamento(orc.getTipoPagamento() != null ? orc.getTipoPagamento().getNome() : null)
                .transformadoEmOS(orc.getTransformadoEmOS())
                .numeroOSGerado(orc.getNumeroOSGerado())
                .build();
    }
}