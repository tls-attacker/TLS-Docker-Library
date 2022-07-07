/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.rub.nds.tls.subject.exceptions;

public class DefaultProfileNotFoundException extends RuntimeException {

    public DefaultProfileNotFoundException() {
    }

    public DefaultProfileNotFoundException(String message) {
        super(message);
    }

    public DefaultProfileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DefaultProfileNotFoundException(Throwable cause) {
        super(cause);
    }

    public DefaultProfileNotFoundException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
