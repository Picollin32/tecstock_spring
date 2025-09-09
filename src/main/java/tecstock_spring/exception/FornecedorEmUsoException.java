package tecstock_spring.exception;

public class FornecedorEmUsoException extends RuntimeException {
    public FornecedorEmUsoException(String message) {
        super(message);
    }
}
