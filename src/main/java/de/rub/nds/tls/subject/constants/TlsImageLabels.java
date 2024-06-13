/*
 * TLS-Docker-Library - A collection of open source TLS clients and servers
 *
 * Copyright 2017-2022 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tls.subject.constants;

public enum TlsImageLabels {
    IMPLEMENTATION("tls_implementation"),
    VERSION("tls_implementation_version"),
    CONNECTION_ROLE("tls_implementation_connectionRole"),
    ADDITIONAL_BUILD_FLAGS("tls_implementation_build_flags");

    private String labelName;

    TlsImageLabels(String label) {
        this.labelName = label;
    }

    public String getLabelName() {
        return this.labelName;
    }
}
