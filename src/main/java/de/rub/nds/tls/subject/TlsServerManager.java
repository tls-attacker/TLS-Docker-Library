package de.rub.nds.tls.subject;

import de.rub.nds.tls.subject.params.ParameterProfile;
import de.rub.nds.tls.subject.properties.ServerImageProperties;

/**
 * Interface to manage a specific type of TLS-Server
 */
public interface TlsServerManager {

    TlsServer getTlsServer(ServerImageProperties properties, ParameterProfile profile, String version);

    TlsServer getTlsServer(ServerImageProperties properties, ParameterProfile profile);

    void killTlsServer(TlsServer tlsServer);

    String getLogsFromTlsServer(TlsServer tlsServer);
}
