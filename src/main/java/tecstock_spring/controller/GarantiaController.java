package tecstock_spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tecstock_spring.dto.GarantiaPaginaDTO;
import tecstock_spring.dto.GarantiaRetornoRequest;
import tecstock_spring.dto.GarantiaResumoDTO;
import tecstock_spring.service.GarantiaService;

@RestController
@RequestMapping("/api/garantias")
@RequiredArgsConstructor
public class GarantiaController {

    private final GarantiaService garantiaService;

    @GetMapping("/buscarPaginado")
    public ResponseEntity<GarantiaPaginaDTO> buscarPaginado(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false, defaultValue = "TODOS") String field,
            @RequestParam(required = false, defaultValue = "TODOS") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        return ResponseEntity.ok(garantiaService.buscarGarantias(query, field, status, page, size));
    }

    @PostMapping("/{ordemServicoId}/retorno")
    public ResponseEntity<GarantiaResumoDTO> registrarRetorno(
            @PathVariable Long ordemServicoId,
            @RequestBody GarantiaRetornoRequest request) {
        return ResponseEntity.ok(
                garantiaService.registrarRetorno(ordemServicoId, request.getServicoId(), request.getMotivo())
        );
    }

    @PutMapping("/retorno/{retornoId}")
    public ResponseEntity<GarantiaResumoDTO> editarRetorno(
            @PathVariable Long retornoId,
            @RequestBody GarantiaRetornoRequest request) {
        return ResponseEntity.ok(garantiaService.editarRetorno(retornoId, request.getMotivo()));
    }

    @DeleteMapping("/retorno/{retornoId}")
    public ResponseEntity<Void> deletarRetorno(@PathVariable Long retornoId) {
        garantiaService.deletarRetorno(retornoId);
        return ResponseEntity.noContent().build();
    }
}
