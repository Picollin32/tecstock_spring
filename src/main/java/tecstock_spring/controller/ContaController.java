package tecstock_spring.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tecstock_spring.model.Conta;
import tecstock_spring.service.ContaService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contas")
public class ContaController {

    private final ContaService contaService;
    private static final Logger logger = LoggerFactory.getLogger(ContaController.class);

    @GetMapping("/mes/{mes}/ano/{ano}")
    public List<Conta> listarPorMesAno(@PathVariable int mes, @PathVariable int ano) {
        logger.info("Listando contas do mês {}/{}", mes, ano);
        return contaService.listarPorMesAno(mes, ano);
    }

    @GetMapping("/a-pagar/mes/{mes}/ano/{ano}")
    public List<Conta> listarAPagar(@PathVariable int mes, @PathVariable int ano) {
        logger.info("Listando contas a pagar {}/{}", mes, ano);
        return contaService.listarAPagarPorMesAno(mes, ano);
    }

    @GetMapping("/a-receber/mes/{mes}/ano/{ano}")
    public List<Conta> listarAReceber(@PathVariable int mes, @PathVariable int ano) {
        logger.info("Listando contas a receber {}/{}", mes, ano);
        return contaService.listarAReceberPorMesAno(mes, ano);
    }

    @GetMapping("/atrasadas")
    public List<Conta> listarAtrasadas() {
        logger.info("Listando contas atrasadas");
        return contaService.listarAtrasadas();
    }

    @GetMapping("/resumo/mes/{mes}/ano/{ano}")
    public Map<String, Double> resumoMes(@PathVariable int mes, @PathVariable int ano) {
        logger.info("Resumo financeiro {}/{}", mes, ano);
        return contaService.resumoMes(mes, ano);
    }


    @PostMapping("/a-pagar")
    public ResponseEntity<Conta> adicionarContaPagar(@RequestBody Conta conta) {
        logger.info("Adicionando conta a pagar: {}", conta.getDescricao());
        Conta salva = contaService.adicionarContaPagar(conta);
        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }

    @PatchMapping("/{id}/pagar")
    public ResponseEntity<Conta> marcarComoPago(@PathVariable Long id) {
        logger.info("Marcando conta {} como paga", id);
        try {
            Conta conta = contaService.marcarComoPago(id);
            return ResponseEntity.ok(conta);
        } catch (RuntimeException e) {
            logger.error("Erro ao pagar conta {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/desmarcar-pagamento")
    public ResponseEntity<Conta> desmarcarPagamento(@PathVariable Long id) {
        logger.info("Desmarcando pagamento da conta {}", id);
        try {
            Conta conta = contaService.desmarcarPagamento(id);
            return ResponseEntity.ok(conta);
        } catch (RuntimeException e) {
            logger.error("Erro ao desmarcar conta {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Conta> editar(@PathVariable Long id, @RequestBody Conta dados) {
        logger.info("Editando conta {}", id);
        try {
            Conta atualizada = contaService.editar(id, dados);
            return ResponseEntity.ok(atualizada);
        } catch (IllegalArgumentException e) {
            logger.warn("Edição negada para conta {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            logger.error("Erro ao editar conta {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        logger.info("Deletando conta {}", id);
        try {
            contaService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("Erro ao deletar conta {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/pagamento-parcial")
    public ResponseEntity<?> registrarPagamentoParcial(@PathVariable Long id, @RequestBody Map<String, Double> body) {
        Double valor = body.get("valor");
        if (valor == null || valor <= 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "Informe um valor de pagamento válido."));
        }
        logger.info("Registrando pagamento parcial de R$ {} na conta {}", valor, id);
        try {
            Conta conta = contaService.registrarPagamentoParcial(id, valor);
            return ResponseEntity.ok(conta);
        } catch (IllegalArgumentException e) {
            logger.warn("Pagamento parcial negado para conta {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            logger.error("Erro ao registrar pagamento parcial na conta {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
