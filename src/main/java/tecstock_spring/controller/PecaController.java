package tecstock_spring.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tecstock_spring.dto.AjusteEstoqueDTO;
import tecstock_spring.model.Peca;
import tecstock_spring.service.PecaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pecas")
@RequiredArgsConstructor
public class PecaController {

    private final PecaService service;
    private static final Logger logger = LoggerFactory.getLogger(PecaController.class);

    @PostMapping("/salvar")
    public Peca salvar(@RequestBody Peca peca) {
        logger.info("Salvando peça: " + peca);
        return service.salvar(peca);
    }

    @GetMapping("/buscar/{id}")
    public Peca buscarPorId(@PathVariable Long id) {
        logger.info("Buscando peça com ID: " + id);
        return service.buscarPorId(id);
    }

    @GetMapping("/buscarPorCodigo/{codigo}")
    public Peca buscarPorCodigo(@PathVariable String codigo) {
        logger.info("Buscando peça com código: " + codigo);
        return service.buscarPorCodigo(codigo);
    }
    
    @GetMapping("/buscarPorCodigo")
    public Peca buscarPorCodigoParam(@RequestParam("codigo") String codigo) {
        logger.info("Buscando peça com código: " + codigo);
        return service.buscarPorCodigo(codigo);
    }

    @GetMapping("/listarTodas")
    public List<Peca> listarTodos() {
        logger.info("Listando todas as peças.");
        return service.listarTodas();
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id, 
            @RequestBody Peca peca, 
            @RequestHeader(value = "X-User-Level", required = false) Integer userLevel) {

        Peca pecaExistente = service.buscarPorId(id);
        if (userLevel != null && userLevel > 1) {
            if (pecaExistente.getQuantidadeEstoque() > 0) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado. Apenas administradores podem editar peças com estoque.");
            }
        }
        
        return ResponseEntity.ok(service.atualizar(id, peca));
    }
    
    @PostMapping("/ajustar-estoque")
    public ResponseEntity<?> ajustarEstoque(@Valid @RequestBody AjusteEstoqueDTO ajusteDTO, @RequestHeader(value = "X-User-Level", required = false) Integer userLevel) {
        if (userLevel == null || userLevel > 1) {
            logger.warn("Acesso negado ao ajustar estoque. Nível: " + userLevel);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado. Apenas administradores podem ajustar estoque.");
        }
        logger.info("Ajustando estoque da peça ID " + ajusteDTO.getPecaId() + " em " + ajusteDTO.getAjuste() + " unidades");
        return ResponseEntity.ok(service.ajustarEstoque(ajusteDTO));
    }

    @DeleteMapping("/deletar/{id}")
    public void deletar(@PathVariable Long id) {
        logger.info("Deletando peça com ID: " + id);
        service.deletar(id);
    }
    
    @GetMapping("/em-uso")
    public List<Peca> listarEmUso() {
        logger.info("Listando peças em uso em OSs não encerradas");
        return service.listarEmUso();
    }
    
    @PostMapping("/atualizar-unidades-usadas")
    public void atualizarUnidadesUsadas() {
        logger.info("Atualizando unidades usadas de todas as peças");
        service.atualizarUnidadesUsadas();
    }
    
    @GetMapping("/buscarPaginado")
    public Page<tecstock_spring.dto.PecaPesquisaDTO> buscarPaginado(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return service.buscarPaginado(query, pageable);
    }
}