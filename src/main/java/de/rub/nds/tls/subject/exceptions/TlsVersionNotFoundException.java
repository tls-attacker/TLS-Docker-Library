/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

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

    public TlsVersionNotFoundException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
