package tecstock_spring.exception;

public class PlacaDuplicadaException extends RuntimeException {
    
    public PlacaDuplicadaException(String message) {
        super(message);
    }
    
    public PlacaDuplicadaException(String message, Throwable cause) {
        super(message, cause);
    }
}
