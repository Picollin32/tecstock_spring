package tecstock_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tecstock_spring.dto.RelatorioAgendamentosDTO;
import tecstock_spring.dto.RelatorioComissaoDTO;
import tecstock_spring.dto.RelatorioEstoqueDTO;
import tecstock_spring.dto.RelatorioFiadoDTO;
import tecstock_spring.dto.RelatorioFinanceiroDTO;
import tecstock_spring.dto.RelatorioGarantiasDTO;
import tecstock_spring.dto.RelatorioServicosDTO;
import tecstock_spring.service.RelatorioService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin(origins = "*")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @GetMapping("/agendamentos")
    public ResponseEntity<RelatorioAgendamentosDTO> gerarRelatorioAgendamentos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        RelatorioAgendamentosDTO relatorio = relatorioService.gerarRelatorioAgendamentos(dataInicio, dataFim);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/servicos")
    public ResponseEntity<RelatorioServicosDTO> gerarRelatorioServicos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        RelatorioServicosDTO relatorio = relatorioService.gerarRelatorioServicos(dataInicio, dataFim);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/estoque")
    public ResponseEntity<RelatorioEstoqueDTO> gerarRelatorioEstoque(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        RelatorioEstoqueDTO relatorio = relatorioService.gerarRelatorioEstoque(dataInicio, dataFim);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/financeiro")
    public ResponseEntity<RelatorioFinanceiroDTO> gerarRelatorioFinanceiro(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        RelatorioFinanceiroDTO relatorio = relatorioService.gerarRelatorioFinanceiro(dataInicio, dataFim);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/comissao")
    public ResponseEntity<RelatorioComissaoDTO> gerarRelatorioComissao(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam Long mecanicoId) {
        
        RelatorioComissaoDTO relatorio = relatorioService.gerarRelatorioComissao(dataInicio, dataFim, mecanicoId);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/garantias")
    public ResponseEntity<RelatorioGarantiasDTO> gerarRelatorioGarantias(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        RelatorioGarantiasDTO relatorio = relatorioService.gerarRelatorioGarantias(dataInicio, dataFim);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/fiado")
    public ResponseEntity<RelatorioFiadoDTO> gerarRelatorioFiado(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        RelatorioFiadoDTO relatorio = relatorioService.gerarRelatorioFiado(dataInicio, dataFim);
        return ResponseEntity.ok(relatorio);
    }
}
