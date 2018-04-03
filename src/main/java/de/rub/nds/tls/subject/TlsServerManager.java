package de.rub.nds.tls.subject;

import de.rub.nds.tls.subject.params.ParameterProfile;
import de.rub.nds.tls.subject.properties.ImageProperties;

/**
 * Interface to manage a specific type of TLS-Servers
 */

public interface TlsServerManager {

    TlsServer getTlsServer(ImageProperties properties, ParameterProfile profile, String version);
    
    TlsServer getTlsServer(ImageProperties properties, ParameterProfile profile);

    void killTlsServer(TlsServer tlsServer);

    String getLogsFromTlsServer(TlsServer tlsServer);
}
