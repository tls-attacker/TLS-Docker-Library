package de.rub.nds.tls.subject;

import de.rub.nds.tls.subject.params.ParameterProfile;
import de.rub.nds.tls.subject.properties.ImageProperties;

/**
 * Interface to manage a specific TLS-Type
 */

public interface TlsInstanceManager {

    TlsInstance getTlsServer(ImageProperties properties, ParameterProfile profile, String host);
    
    TlsInstance getTlsServer(ImageProperties properties, ParameterProfile profile, String version, String host);
    
    TlsInstance getTlsClient(ImageProperties properties, ParameterProfile profile, String host, int port);
    
    TlsInstance getTlsClient(ImageProperties properties, ParameterProfile profile, String version, String host, int port);
    
    //TlsInstance getTlsInstance(ConnectionRole role, ImageProperties properties, ParameterProfile profile, String version, String host, int port);

    void killTlsInstance(TlsInstance tlsInstance);

    String getLogsFromTlsInstance(TlsInstance tlsInstance);
    
}
