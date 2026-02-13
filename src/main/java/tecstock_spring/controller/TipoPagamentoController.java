package tecstock_spring.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tecstock_spring.model.TipoPagamento;
import tecstock_spring.service.TipoPagamentoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TipoPagamentoController {

    private final TipoPagamentoService service;
    Logger logger = LoggerFactory.getLogger(TipoPagamentoController.class);

    @PostMapping("/api/tipos-pagamento/salvar")
    public ResponseEntity<?> salvar(@RequestBody TipoPagamento tipoPagamento, @RequestHeader(value = "X-User-Level", required = false) Integer userLevel) {
        if (userLevel == null || userLevel > 1) {
            logger.warn("Acesso negado ao salvar tipo de pagamento. Nível: " + userLevel);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado. Apenas administradores podem criar tipos de pagamento.");
        }
        logger.info("Salvando tipo de pagamento: " + tipoPagamento + " no controller.");
        return ResponseEntity.ok(service.salvar(tipoPagamento));
    }

    @GetMapping("/api/tipos-pagamento/buscar/{id}")
    public TipoPagamento buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/api/tipos-pagamento/listarTodos")
    public List<TipoPagamento> listarTodos() {
        logger.info("Listando tipos de pagamento no controller.");
        return service.listarTodos();
    }

    @PutMapping("/api/tipos-pagamento/atualizar/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody TipoPagamento tipoPagamento, @RequestHeader(value = "X-User-Level", required = false) Integer userLevel) {
        if (userLevel == null || userLevel > 1) {
            logger.warn("Acesso negado ao atualizar tipo de pagamento. Nível: " + userLevel);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado. Apenas administradores podem atualizar tipos de pagamento.");
        }
        logger.info("Atualizando tipo de pagamento no controller. ID: " + id + ", Tipo de pagamento: " + tipoPagamento);
        return ResponseEntity.ok(service.atualizar(id, tipoPagamento));
    }

    @DeleteMapping("/api/tipos-pagamento/deletar/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id, @RequestHeader(value = "X-User-Level", required = false) Integer userLevel) {
        if (userLevel == null || userLevel > 1) {
            logger.warn("Acesso negado ao deletar tipo de pagamento. Nível: " + userLevel);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado. Apenas administradores podem deletar tipos de pagamento.");
        }
        logger.info("Deletando tipo de pagamento no controller. ID: " + id);
        service.deletar(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/api/tipos-pagamento/buscarPaginado")
    public Object buscarPaginado(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        // Se não há pesquisa, retorna os últimos 6 cadastros sem paginação
        if (query == null || query.trim().isEmpty()) {
            List<tecstock_spring.dto.TipoPagamentoPesquisaDTO> lista = service.listarUltimosParaInicio(6);
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("content", lista);
            response.put("totalElements", lista.size());
            response.put("totalPages", 1);
            response.put("number", 0);
            return response;
        }
        // Com pesquisa, usa paginação normal
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return service.buscarPaginado(query, pageable);
    }}