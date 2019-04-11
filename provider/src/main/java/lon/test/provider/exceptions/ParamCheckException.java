package lon.test.provider.exceptions;

public class ParamCheckException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ParamCheckException() {
        super();
    }

    public ParamCheckException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ParamCheckException(String message) {
        super(message);
    }

    public ParamCheckException(Throwable throwable) {
        super(throwable);
    }

}
