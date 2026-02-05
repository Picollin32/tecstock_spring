package tecstock_spring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Inválido")
            .findFirst()
            .orElse("Dados inválidos fornecidos");

        Map<String, Object> body = new HashMap<>();
        body.put("message", message);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CodigoPecaDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> handleCodigoPecaDuplicadoException(
            CodigoPecaDuplicadoException ex, WebRequest request) {
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));
        body.put("error", "Código de Peça Duplicado");
        
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CpfDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> handleCpfDuplicadoException(
            CpfDuplicadoException ex, WebRequest request) {
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));
        body.put("error", "CPF Duplicado");
        
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NomeDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> handleNomeDuplicadoException(
            NomeDuplicadoException ex, WebRequest request) {
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));
        body.put("error", "Nome Duplicado");
        
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PlacaDuplicadaException.class)
    public ResponseEntity<Map<String, Object>> handlePlacaDuplicadaException(
            PlacaDuplicadaException ex, WebRequest request) {
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));
        body.put("error", "Placa Duplicada");
        
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(VeiculoEmUsoException.class)
    public ResponseEntity<Map<String, Object>> handleVeiculoEmUsoException(
            VeiculoEmUsoException ex, WebRequest request) {
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));
        body.put("error", "Veículo em Uso");
        
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MarcaEmUsoException.class)
    public ResponseEntity<Map<String, Object>> handleMarcaEmUsoException(
            MarcaEmUsoException ex, WebRequest request) {
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));
        body.put("error", "Marca em Uso");
        
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(FornecedorEmUsoException.class)
    public ResponseEntity<Map<String, Object>> handleFornecedorEmUsoException(
            FornecedorEmUsoException ex, WebRequest request) {
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));
        body.put("error", "Fornecedor em Uso");
        
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(FabricanteEmUsoException.class)
    public ResponseEntity<Map<String, Object>> handleFabricanteEmUsoException(
            FabricanteEmUsoException ex, WebRequest request) {
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));
        body.put("error", "Fabricante em Uso");
        
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PecaComEstoqueException.class)
    public ResponseEntity<Map<String, Object>> handlePecaComEstoqueException(
            PecaComEstoqueException ex, WebRequest request) {
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));
        body.put("error", "Peça com Estoque");
        
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TipoPagamentoEmUsoException.class)
    public ResponseEntity<Map<String, Object>> handleTipoPagamentoEmUsoException(
            TipoPagamentoEmUsoException ex, WebRequest request) {
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));
        body.put("error", "Tipo de Pagamento em Uso");
        
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ClienteEmUsoException.class)
    public ResponseEntity<Map<String, Object>> handleClienteEmUsoException(
            ClienteEmUsoException ex, WebRequest request) {
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));
        body.put("error", "Cliente em Uso");
        
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(FuncionarioEmUsoException.class)
    public ResponseEntity<Map<String, Object>> handleFuncionarioEmUsoException(
            FuncionarioEmUsoException ex, WebRequest request) {
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));
        body.put("error", "Funcionário em Uso");
        
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(OrdemServicoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleOrdemServicoNotFoundException(
            OrdemServicoNotFoundException ex, WebRequest request) {
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));
        body.put("error", "Ordem de Serviço Não Encontrada");
        
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NumeroOSDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> handleNumeroOSDuplicadoException(
            NumeroOSDuplicadoException ex, WebRequest request) {
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));
        body.put("error", "Número de OS Duplicado");
        
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));
        body.put("error", "Dados Inválidos");
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        
        String message = ex.getMessage() != null ? ex.getMessage() : "Erro interno do servidor";
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", message);
        body.put("path", request.getDescription(false));
        
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (message.contains("não encontrado")) {
            status = HttpStatus.NOT_FOUND;
        } else if (message.contains("já cadastrado")) {
            status = HttpStatus.CONFLICT;
        }
        
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, WebRequest request) {
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "Erro interno do servidor");
        body.put("details", ex.getMessage());
        body.put("path", request.getDescription(false));
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
