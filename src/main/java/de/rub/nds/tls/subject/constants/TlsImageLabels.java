/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tls.subject.constants;

public enum TlsImageLabels {
    IMPLEMENTATION("tls_implementation"),
    VERSION("tls_implementation_version"),
    CONNECTION_ROLE("tls_implementation_connectionRole");

    private String labelName;

    TlsImageLabels(String label) {
        this.labelName = label;
    }

    public String getLabelName() {
        return this.labelName;
    }
}
