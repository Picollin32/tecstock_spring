package tecstock_spring.service;

import tecstock_spring.dto.GarantiaPaginaDTO;
import tecstock_spring.dto.GarantiaResumoDTO;

public interface GarantiaService {

    GarantiaPaginaDTO buscarGarantias(String query, String field, String status, int page, int size);

    GarantiaResumoDTO registrarRetorno(Long ordemServicoId, Long servicoId, String motivo);

    GarantiaResumoDTO editarRetorno(Long retornoId, String motivo);

    void deletarRetorno(Long retornoId);
}
