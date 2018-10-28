package de.rub.nds.tls.subject.properties;

import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.TlsImplementationType;

public class ImageProperties {
    
    private final ConnectionRole role;
    private final TlsImplementationType type;
    private final String defaultVersion;
    private final Integer internalPort;
    private final String defaultKeyPath;
    private final String defaultCertPath;
    private final boolean useIP;
    
    public ImageProperties(ConnectionRole role, TlsImplementationType type, String defaultVersion, String defaultCertPath, boolean useIP) {
        this.role = role;
        this.type = type;
        this.defaultVersion = defaultVersion;
        this.internalPort = null;
        this.defaultKeyPath = null;
        this.defaultCertPath = defaultCertPath;
        this.useIP = useIP;
    }

    public ImageProperties(ConnectionRole role, TlsImplementationType type, String defaultVersion, Integer internalPort, String defaultKeyPath, String defaultCertPath) {
        this.role = role;
        this.type = type;
        this.defaultVersion = defaultVersion;
        this.internalPort = internalPort;
        this.defaultKeyPath = defaultKeyPath;
        this.defaultCertPath = defaultCertPath;
        this.useIP = true;
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
    
    public Integer getInternalPort() {
        return internalPort;
    }

    public String getDefaultKeyPath() {
        return defaultKeyPath;
    }

    public String getDefaultCertPath() {
        return defaultCertPath;
    }

    public boolean isUseIP() {
        return useIP;
    }
}
