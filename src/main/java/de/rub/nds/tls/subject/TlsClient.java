package de.rub.nds.tls.subject;

/**
 * The representation of a TLS-Client used for a Test
 */
public class TlsClient {

    private final String id;
    private final String host;
    private final int port;
    private int exitCode;
    private final String name;
    private final TlsClientManager tlsClientManager;

    public TlsClient(String id, String host, int port, String name, TlsClientManager tlsClientManager) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.name = name;
        this.tlsClientManager = tlsClientManager;
    }

    public String getClientName() {
        return name;
    }

    public String getClientLogs() {
        return tlsClientManager.getLogsFromTlsClient(this);
    }

    public String getExitInfo() {
        return "exitCode: " + exitCode;
    }

    public void kill() {
        tlsClientManager.killTlsClient(this);
    }

    @Override
    public String toString() {
        return String.format("%s (%s:%d)", getClientName(), host, port);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public String getId() {
        return id;
    }
}
