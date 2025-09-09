package tecstock_spring.exception;

public class CpfDuplicadoException extends RuntimeException {
    
    public CpfDuplicadoException(String message) {
        super(message);
    }
    
    public CpfDuplicadoException(String message, Throwable cause) {
        super(message, cause);
    }
}
