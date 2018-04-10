package de.rub.nds.tls.subject.exceptions;

public class ImplementationDidNotStartException extends RuntimeException {

    public ImplementationDidNotStartException() {
    }

    public ImplementationDidNotStartException(String message) {
        super(message);
    }

    public ImplementationDidNotStartException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImplementationDidNotStartException(Throwable cause) {
        super(cause);
    }

    public ImplementationDidNotStartException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
