package de.rub.nds.tls.subject;

/**
 * Interface to manage a specific type of TLS-Servers
 */

public interface TlsServerManager {

    TlsServer getTlsServer();

    void killTlsServer(TlsServer tlsServer);

    String getLogsFromTlsServer(TlsServer tlsServer);

    String getServerName(TlsServer tlsServer);
}
