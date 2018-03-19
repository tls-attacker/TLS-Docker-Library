package de.rub.nds.tls.subject.exceptions;

public class CertVolumeNotFoundException extends RuntimeException {

    public CertVolumeNotFoundException() {
    }

    public CertVolumeNotFoundException(String message) {
        super(message);
    }

    public CertVolumeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CertVolumeNotFoundException(Throwable cause) {
        super(cause);
    }

    public CertVolumeNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
