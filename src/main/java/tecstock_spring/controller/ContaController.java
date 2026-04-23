package tecstock_spring.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tecstock_spring.dto.ContaComParcelasDTO;
import tecstock_spring.model.Conta;
import tecstock_spring.service.ContaService;

import java.time.LocalDate;
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

    @GetMapping("/{id}/com-parcelas")
    public ResponseEntity<ContaComParcelasDTO> buscarContaComParcelas(@PathVariable Long id) {
        logger.info("Buscando conta {} com parcelas", id);
        try {
            ContaComParcelasDTO dto = contaService.buscarContaComParcelas(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            logger.error("Erro ao buscar conta {} com parcelas: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/a-pagar")
    public ResponseEntity<Conta> adicionarContaPagar(@RequestBody Conta conta) {
        logger.info("Adicionando conta a pagar: {}", conta.getDescricao());
        Conta salva = contaService.adicionarContaPagar(conta);
        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }

    @PatchMapping("/{id}/pagar")
    public ResponseEntity<?> marcarComoPago(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> body) {
        logger.info("Marcando conta {} como paga", id);
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

            Conta conta = contaService.marcarComoPago(id, dataPagamento, acrescimo, desconto);
            return ResponseEntity.ok(conta);
        } catch (IllegalArgumentException e) {
            logger.warn("Validação ao pagar conta {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
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
    public ResponseEntity<Conta> editar(@PathVariable Long id, @RequestBody Map<String, Object> dados) {
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

    @PostMapping("/a-pagar/frete")
    public ResponseEntity<?> adicionarFrete(@RequestBody Map<String, Object> body) {
        logger.info("Adicionando frete avulso");
        try {
            String descricao = body.get("descricao").toString();
            double valor = Double.parseDouble(body.get("valor").toString());
            @SuppressWarnings("unchecked")
            Map<String, Object> pagamento = (Map<String, Object>) body.get("pagamento");
            if (pagamento == null || pagamento.get("formaPagamento") == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Forma de pagamento obrigatória"));
            }
            if (body.get("categoriaFinanceiraId") != null) {
                pagamento.put("categoriaFinanceiraId", body.get("categoriaFinanceiraId"));
            }
            if (body.get("fornecedorId") != null) {
                pagamento.put("fornecedorId", body.get("fornecedorId"));
            }
            pagamento.put("origemTipoBase", "FRETE");
            contaService.gerarContasParaCompra(pagamento, valor, descricao);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("sucesso", true));
        } catch (Exception e) {
            logger.error("Erro ao adicionar frete: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/a-pagar/lancamento")
    public ResponseEntity<?> adicionarLancamentoAPagar(@RequestBody Map<String, Object> body) {
        logger.info("Adicionando lançamento avulso a pagar");
        try {
            String descricao = body.get("descricao").toString();
            double valor = Double.parseDouble(body.get("valor").toString());
            String origem = body.getOrDefault("origem", "DESPESA").toString();
            @SuppressWarnings("unchecked")
            Map<String, Object> pagamento = (Map<String, Object>) body.get("pagamento");
            if (pagamento == null || pagamento.get("formaPagamento") == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Forma de pagamento obrigatória"));
            }
            if (body.get("categoriaFinanceiraId") != null) {
                pagamento.put("categoriaFinanceiraId", body.get("categoriaFinanceiraId"));
            }
            if (body.get("fornecedorId") != null) {
                pagamento.put("fornecedorId", body.get("fornecedorId"));
            }
            pagamento.put("origemTipoBase", origem);
            contaService.gerarContasParaCompra(pagamento, valor, descricao);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("sucesso", true));
        } catch (Exception e) {
            logger.error("Erro ao adicionar lançamento: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
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
