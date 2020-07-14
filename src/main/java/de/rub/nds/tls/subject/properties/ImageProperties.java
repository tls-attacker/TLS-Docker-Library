package de.rub.nds.tls.subject.properties;

import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.TlsImplementationType;

public class ImageProperties {

    private final ConnectionRole role;
    private final TlsImplementationType type;
    private final String defaultVersion;

    private Integer internalPort;
    private String defaultKeyPath;
    private String defaultCertPath;
    private String defaultCertKeyCombinedPath;
    private boolean useIP;

    public ImageProperties(ConnectionRole role, TlsImplementationType type, String defaultVersion, String defaultCertPath, boolean useIP) {
        // Called for TLS client images
        this.role = role;
        this.type = type;
        this.defaultVersion = defaultVersion;
        this.internalPort = null;
        this.defaultKeyPath = null;
        this.defaultCertPath = defaultCertPath;
        this.useIP = useIP;
    }

    public ImageProperties(ConnectionRole role, TlsImplementationType type, String defaultVersion, Integer internalPort, String defaultKeyPath, String defaultCertPath, String defaultCertKeyCombindPath) {
        // Called for TLS server images
        this.role = role;
        this.type = type;
        this.defaultVersion = defaultVersion;
        this.internalPort = internalPort;
        this.defaultKeyPath = defaultKeyPath;
        this.defaultCertPath = defaultCertPath;
        this.defaultCertKeyCombinedPath = defaultCertKeyCombindPath;
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

    public void setInternalPort(Integer internalPort) {
        this.internalPort = internalPort;
    }

    public void setDefaultKeyPath(String defaultKeyPath) {
        this.defaultKeyPath = defaultKeyPath;
    }

    public void setDefaultCertPath(String defaultCertPath) {
        this.defaultCertPath = defaultCertPath;
    }

    public void setUseIP(boolean useIP) {
        this.useIP = useIP;
    }

    public String getDefaultCertKeyCombinedPath() {
        return defaultCertKeyCombinedPath;
    }
}
