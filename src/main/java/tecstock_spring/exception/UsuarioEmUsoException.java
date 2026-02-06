package tecstock_spring.exception;

public class UsuarioEmUsoException extends RuntimeException {
    public UsuarioEmUsoException(String message) {
        super(message);
    }
}
