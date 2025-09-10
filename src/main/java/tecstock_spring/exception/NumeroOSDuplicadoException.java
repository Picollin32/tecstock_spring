package tecstock_spring.exception;

public class NumeroOSDuplicadoException extends RuntimeException {
    public NumeroOSDuplicadoException(String message) {
        super(message);
    }
    
    public NumeroOSDuplicadoException(String message, Throwable cause) {
        super(message, cause);
    }
}
