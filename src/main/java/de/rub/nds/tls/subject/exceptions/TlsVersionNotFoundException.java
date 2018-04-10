package de.rub.nds.tls.subject.exceptions;

public class TlsVersionNotFoundException extends RuntimeException {

    public TlsVersionNotFoundException() {
    }

    public TlsVersionNotFoundException(String message) {
        super(message);
    }

    public TlsVersionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TlsVersionNotFoundException(Throwable cause) {
        super(cause);
    }

    public TlsVersionNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
