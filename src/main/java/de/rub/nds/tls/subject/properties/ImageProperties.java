package de.rub.nds.tls.subject.properties;

import de.rub.nds.tls.subject.TlsImplementationType;

public class ImageProperties {

    private final TlsImplementationType type;

    private final int internalPort;

    private final String defaultVersion;

    private final String defaultKeyPath;

    private final String defaultCertPath;
    
    public ImageProperties(TlsImplementationType type, String defaultVersion) {
        this.type = type;
        this.internalPort = 0;
        this.defaultVersion = defaultVersion;
        this.defaultKeyPath = null;
        this.defaultCertPath = null;
    }

    public ImageProperties(TlsImplementationType type, int internalPort, String defaultVersion, String defaultCertPath, String defaultKeyPath) {
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
