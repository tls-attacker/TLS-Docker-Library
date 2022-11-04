/*
 * TLS-Docker-Library - A collection of open source TLS clients and servers
 *
 * Copyright 2017-2022 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tls.subject.report;

import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.TlsImplementationType;
import java.io.Serializable;

/**
 * @author robert
 */
public class InstanceContainer implements Serializable {

    private ConnectionRole role;

    private TlsImplementationType implementationType;

    private String version;

    private boolean functional;

    private InstanceContainer() {}

    public InstanceContainer(
            ConnectionRole role,
            TlsImplementationType implementationType,
            String version,
            boolean functional) {
        this.role = role;
        this.implementationType = implementationType;
        this.version = version;
        this.functional = functional;
    }

    public ConnectionRole getRole() {
        return role;
    }

    public void setRole(ConnectionRole role) {
        this.role = role;
    }

    public TlsImplementationType getImplementationType() {
        return implementationType;
    }

    public void setImplementationType(TlsImplementationType implementationType) {
        this.implementationType = implementationType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isFunctional() {
        return functional;
    }

    public void setFunctional(boolean functional) {
        this.functional = functional;
    }
}
