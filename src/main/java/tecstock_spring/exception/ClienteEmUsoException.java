package tecstock_spring.exception;

public class ClienteEmUsoException extends RuntimeException {
    public ClienteEmUsoException(String message) {
        super(message);
    }
}
