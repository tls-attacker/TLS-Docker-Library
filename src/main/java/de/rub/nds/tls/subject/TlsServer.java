package de.rub.nds.tls.subject;

/**
 * The representation of a TLS-Server used for a Test
 */

public class TlsServer {

    private final String id;
    private final String host = "127.0.0.42";
    private final int port;
    private int exitCode;
    private final String name;
    private final TlsServerManager tlsServerManager;

    public TlsServer(String id, int port, String name, TlsServerManager tlsServerManager) {
        this.id = id;
        this.port = port;
        this.name = name;
        this.tlsServerManager = tlsServerManager;
    }

    public String getServerName() {
        return name;
    }

    public String getServerLogs() {
        return tlsServerManager.getLogsFromTlsServer(this);
    }

    public String getExitInfo() {
        return "exitCode: " + exitCode;
    }

    public void kill() {
        tlsServerManager.killTlsServer(this);
    }

    @Override
    public String toString() {
        return String.format("%s:%d (%s)", host, port, getServerName());
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
