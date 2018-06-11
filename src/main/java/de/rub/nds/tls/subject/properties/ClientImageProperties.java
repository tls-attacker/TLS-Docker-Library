package de.rub.nds.tls.subject.properties;

import de.rub.nds.tls.subject.TlsImplementationType;

public class ClientImageProperties {

    private final TlsImplementationType type;

    private final String defaultVersion;

    public ClientImageProperties(TlsImplementationType type, String defaultVersion) {
        this.type = type;
        this.defaultVersion = defaultVersion;
    }

    public TlsImplementationType getType() {
        return type;
    }

    public String defaultVersion() {
        return defaultVersion;
    }

    public String getDefaultVersion() {
        return defaultVersion;
    }
}
