/*
 * TLS-Docker-Library - A collection of open source TLS clients and servers
 *
 * Copyright 2017-2022 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tls.subject.constants;

import com.github.dockerjava.api.model.InternetProtocol;

/**
 * @author robert
 */
public enum TransportType {
    UDP,
    TCP;

    public InternetProtocol toInternetProtocol() {
        switch (this) {
            case UDP:
                return InternetProtocol.UDP;
            case TCP:
                return InternetProtocol.TCP;
        }
        return null;
    }
}
