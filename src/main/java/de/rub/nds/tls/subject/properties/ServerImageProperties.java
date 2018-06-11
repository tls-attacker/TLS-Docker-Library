package de.rub.nds.tls.subject.properties;

import de.rub.nds.tls.subject.TlsImplementationType;

public class ServerImageProperties {

    private final TlsImplementationType type;

    private final int internalPort;

    private final String defaultVersion;

    private final String defaultKeyPath;

    private final String defaultCertPath;

    public ServerImageProperties(TlsImplementationType type, int internalPort, String defaultVersion, String defaultCertPath, String defaultKeyPath) {
        this.type = type;
        this.internalPort = internalPort;
        this.defaultVersion = defaultVersion;
        this.defaultCertPath = defaultCertPath;
        this.defaultKeyPath = defaultKeyPath;
    }

    public TlsImplementationType getType() {
        return type;
    }

    public int getInternalPort() {
        return internalPort;
    }

    public String defaultVersion() {
        return defaultVersion;
    }

    public String getDefaultVersion() {
        return defaultVersion;
    }

    public String getDefaultKeyPath() {
        return defaultKeyPath;
    }

    public String getDefaultCertPath() {
        return defaultCertPath;
    }
}
