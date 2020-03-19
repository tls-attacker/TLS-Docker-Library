package de.rub.nds.tls.subject;

import de.rub.nds.tls.subject.constants.TransportType;

/**
 * The representation of a TLS-Instance used for a Test
 */
public class TlsInstance {

    private final String id;
    private final ConnectionRole role;
    private final String host;
    private Integer port;
    private final String name;
    private final TlsInstanceManager tlsInstanceManager;
    private long exitCode;
    private final HostInfo hostInfo;

    public TlsInstance(String id, ConnectionRole role, String host, Integer port, String name, TlsInstanceManager tlsInstanceManager, HostInfo hostInfo) {
        this.id = id;
        this.role = role;
        this.host = host;
        this.port = port;
        this.name = name;
        this.tlsInstanceManager = tlsInstanceManager;
        this.hostInfo = hostInfo;
    }

    public HostInfo getHostInfo() {
        return hostInfo;
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

    public Integer getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public String getLogs() {
        return tlsInstanceManager.getLogsFromTlsInstance(this);
    }

    public long getExitCode() {
        return exitCode;
    }

    public String getExitInfo() {
        return "exitCode: " + exitCode;
    }

    public void setExitCode(long exitCode) {
        this.exitCode = exitCode;
    }

    public void kill() {
        tlsInstanceManager.killTlsInstance(this);
    }

    public void restart() {
        tlsInstanceManager.restartInstance(this);
    }

    @Override
    public String toString() {
        return String.format("%s: %s:%d (%s)", getConnectionRole().name(), host, port, getName());
    }

    public void start() {
        tlsInstanceManager.startInstance(this);
    }

    public void stop() {
        tlsInstanceManager.stopInstance(this);
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
