package tecstock_spring.exception;

public class NomeDuplicadoException extends RuntimeException {
    
    public NomeDuplicadoException(String message) {
        super(message);
    }
    
    public NomeDuplicadoException(String message, Throwable cause) {
        super(message, cause);
    }
}
