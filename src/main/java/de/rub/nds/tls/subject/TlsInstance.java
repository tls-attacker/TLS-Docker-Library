package de.rub.nds.tls.subject;

/**
 * The representation of a TLS-Instance used for a Test
 */

public class TlsInstance {

    private final String id;
    private final ConnectionRole role;
    private final String host;
    private final int port;
    private final String name;
    private final TlsInstanceManager tlsInstanceManager;
    private int exitCode;

    public TlsInstance(String id, ConnectionRole role, String host, int port, String name, TlsInstanceManager tlsInstanceManager) {
        this.id = id;
        this.role = role;
        this.host = host;
        this.port = port;
        this.name = name;
        this.tlsInstanceManager = tlsInstanceManager;
    }
    
    public String getId() {
        return id;
    }
    
    public ConnectionRole getConnectionRole() {
        return role;
    }
    
    public String getHost() {
        return host;
    }
    
    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public String getLogs() {
        return tlsInstanceManager.getLogsFromTlsInstance(this);
    }
    
    public int getExitCode() {
        return exitCode;
    }

    public String getExitInfo() {
        return "exitCode: " + exitCode;
    }
    
    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public void kill() {
        tlsInstanceManager.killTlsInstance(this);
    }

    @Override
    public String toString() {
        return String.format("%s: %s:%d (%s)", getConnectionRole().name(), host, port, getName());
    }
    
}
