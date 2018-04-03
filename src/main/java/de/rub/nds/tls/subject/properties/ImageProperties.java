package de.rub.nds.tls.subject.properties;

import de.rub.nds.tls.subject.TlsImplementationType;

public class ImageProperties {

    private final TlsImplementationType type;

    private final int internalPort;

    private final String defaultVersion;
    
    private final String defaultKeyPath;
    
    private final String defaultCertPath;

    public ImageProperties(TlsImplementationType type, int internalPort, String defaultVersion, String defaultKeyPath, String defaultCertPath) {
        this.type = type;
        this.internalPort = internalPort;
        this.defaultVersion = defaultVersion;
        this.defaultKeyPath = defaultKeyPath;
        this.defaultCertPath = defaultCertPath;
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
