package tecstock_spring.exception;

public class OrdemServicoNotFoundException extends RuntimeException {
    public OrdemServicoNotFoundException(String message) {
        super(message);
    }
    
    public OrdemServicoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
