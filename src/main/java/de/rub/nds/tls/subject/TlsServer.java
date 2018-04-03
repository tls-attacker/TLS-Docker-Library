package de.rub.nds.tls.subject;

/**
 * The representation of a TLS-Server used for a Test
 */

public class TlsServer {

    public String id;
    public String host = "127.0.0.42";
    public int port;
    public int exitCode;
    private String name;
    private TlsServerManager tlsServerManager;

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
}
