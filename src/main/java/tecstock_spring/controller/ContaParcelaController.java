package tecstock_spring.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tecstock_spring.dto.ContaParcelaDTO;
import tecstock_spring.dto.ContaParcelaEdicaoDTO;
import tecstock_spring.service.ContaService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ContaParcelaController {

    private static final Logger logger = LoggerFactory.getLogger(ContaParcelaController.class);
    private final ContaService contaService;

    @GetMapping("/api/contas/{idConta}/parcelas")
    public ResponseEntity<List<ContaParcelaDTO>> listarParcelasDaConta(@PathVariable Long idConta) {
        logger.info("Listando parcelas da conta {}", idConta);
        try {
            return ResponseEntity.ok(contaService.listarParcelasDaConta(idConta));
        } catch (RuntimeException e) {
            logger.error("Erro ao listar parcelas da conta {}: {}", idConta, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/api/parcelas/{idParcela}/pagar")
    public ResponseEntity<?> marcarParcelaComoPaga(@PathVariable Long idParcela,
                                                   @RequestBody(required = false) Map<String, Object> body) {
        logger.info("Marcando parcela {} como paga", idParcela);
        try {
            LocalDate dataPagamento = LocalDate.now();
            Double acrescimo = null;
            Double desconto = null;

            if (body != null) {
                if (body.get("dataPagamento") != null) {
                    dataPagamento = LocalDate.parse(body.get("dataPagamento").toString());
                }
                if (body.get("acrescimo") != null) {
                    acrescimo = Double.parseDouble(body.get("acrescimo").toString());
                }
                if (body.get("desconto") != null) {
                    desconto = Double.parseDouble(body.get("desconto").toString());
                }
            }

            ContaParcelaDTO dto = contaService.marcarParcelaComoPaga(idParcela, dataPagamento, acrescimo, desconto);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            logger.warn("Validação ao pagar parcela {}: {}", idParcela, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            logger.error("Erro ao pagar parcela {}: {}", idParcela, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/api/parcelas/{idParcela}/desmarcar-pagamento")
    public ResponseEntity<ContaParcelaDTO> desmarcarPagamentoParcela(@PathVariable Long idParcela) {
        logger.info("Desmarcando pagamento da parcela {}", idParcela);
        try {
            return ResponseEntity.ok(contaService.desmarcarPagamentoParcela(idParcela));
        } catch (RuntimeException e) {
            logger.error("Erro ao desmarcar parcela {}: {}", idParcela, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/api/parcelas/{idParcela}")
    public ResponseEntity<?> editarParcela(@PathVariable Long idParcela,
                                           @RequestBody ContaParcelaEdicaoDTO dados) {
        logger.info("Editando parcela {}", idParcela);
        try {
            return ResponseEntity.ok(contaService.editarParcela(idParcela, dados));
        } catch (IllegalArgumentException e) {
            logger.warn("Edição negada para parcela {}: {}", idParcela, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            logger.error("Erro ao editar parcela {}: {}", idParcela, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/api/parcelas/{idParcela}")
    public ResponseEntity<Void> deletarParcela(@PathVariable Long idParcela) {
        logger.info("Deletando parcela {}", idParcela);
        try {
            contaService.deletarParcela(idParcela);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("Erro ao deletar parcela {}: {}", idParcela, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
