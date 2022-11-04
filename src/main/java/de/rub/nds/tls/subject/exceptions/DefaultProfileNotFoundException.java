/*
 * TLS-Docker-Library - A collection of open source TLS clients and servers
 *
 * Copyright 2017-2022 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tls.subject.exceptions;

public class DefaultProfileNotFoundException extends RuntimeException {

    public DefaultProfileNotFoundException() {}

    public DefaultProfileNotFoundException(String message) {
        super(message);
    }

    public DefaultProfileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DefaultProfileNotFoundException(Throwable cause) {
        super(cause);
    }

    public DefaultProfileNotFoundException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
