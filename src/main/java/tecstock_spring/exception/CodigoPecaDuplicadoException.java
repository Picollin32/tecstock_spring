package tecstock_spring.exception;

public class CodigoPecaDuplicadoException extends RuntimeException {
    
    public CodigoPecaDuplicadoException(String message) {
        super(message);
    }
    
    public CodigoPecaDuplicadoException(String message, Throwable cause) {
        super(message, cause);
    }
}
