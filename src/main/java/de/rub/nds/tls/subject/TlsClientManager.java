package de.rub.nds.tls.subject;

import de.rub.nds.tls.subject.params.ParameterProfile;
import de.rub.nds.tls.subject.properties.ClientImageProperties;

/**
 * Interface to manage a specific type of TLS-Client
 */
public interface TlsClientManager {

    TlsClient getTlsClient(ClientImageProperties properties, ParameterProfile profile, String version, String host, int port);

    TlsClient getTlsClient(ClientImageProperties properties, ParameterProfile profile, String host, int port);

    void killTlsClient(TlsClient tlsClient);

    String getLogsFromTlsClient(TlsClient tlsClient);
}
