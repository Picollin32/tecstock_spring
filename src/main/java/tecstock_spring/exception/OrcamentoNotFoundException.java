package tecstock_spring.exception;

public class OrcamentoNotFoundException extends RuntimeException {
    public OrcamentoNotFoundException(String message) {
        super(message);
    }
}