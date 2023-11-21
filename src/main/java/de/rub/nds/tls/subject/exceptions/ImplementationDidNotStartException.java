/*
 * TLS-Docker-Library - A collection of open source TLS clients and servers
 *
 * Copyright 2017-2022 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tls.subject.exceptions;

public class ImplementationDidNotStartException extends RuntimeException {

    public ImplementationDidNotStartException() {}

    public ImplementationDidNotStartException(String message) {
        super(message);
    }

    public ImplementationDidNotStartException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImplementationDidNotStartException(Throwable cause) {
        super(cause);
    }

    public ImplementationDidNotStartException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
