package tecstock_spring.exception;

public class FuncionarioEmUsoException extends RuntimeException {
    public FuncionarioEmUsoException(String message) {
        super(message);
    }
}
