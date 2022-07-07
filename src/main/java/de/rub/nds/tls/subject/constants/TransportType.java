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

package de.rub.nds.tls.subject.constants;

import com.github.dockerjava.api.model.InternetProtocol;

/**
 *
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
