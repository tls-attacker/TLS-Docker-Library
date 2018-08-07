package de.rub.nds.tls.subject.properties;

import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.TlsImplementationType;

public class ImageProperties {
    
    private final ConnectionRole role;

    private final TlsImplementationType type;

    private final String defaultVersion;
    
    private final int internalPort;

    private final String defaultKeyPath;

    private final String defaultCertPath;
    
    public ImageProperties(ConnectionRole role, TlsImplementationType type, String defaultVersion) {
        this.role = role;
        this.type = type;
        this.defaultVersion = defaultVersion;
        this.internalPort = 0;
        this.defaultKeyPath = null;
        this.defaultCertPath = null;
    }

    public ImageProperties(ConnectionRole role, TlsImplementationType type, String defaultVersion, int internalPort, String defaultCertPath, String defaultKeyPath) {
        this.role = role;
        this.type = type;
        this.defaultVersion = defaultVersion;
        this.internalPort = internalPort;
        this.defaultCertPath = defaultCertPath;
        this.defaultKeyPath = defaultKeyPath;
    }

    public ConnectionRole getRole() {
        return role;
    }

    public TlsImplementationType getType() {
        return type;
    }
    
    public String getDefaultVersion() {
        return defaultVersion;
    }
    
    public int getInternalPort() {
        return internalPort;
    }

    public String getDefaultKeyPath() {
        return defaultKeyPath;
    }

    public String getDefaultCertPath() {
        return defaultCertPath;
    }
}
