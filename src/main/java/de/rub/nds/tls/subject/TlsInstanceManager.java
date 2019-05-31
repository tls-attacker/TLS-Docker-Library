package de.rub.nds.tls.subject;

import de.rub.nds.tls.subject.params.ParameterProfile;
import de.rub.nds.tls.subject.properties.ImageProperties;

/**
 * Interface to manage a specific TLS-Instance
 */
public interface TlsInstanceManager {

    TlsInstance getTlsInstance(ConnectionRole role, ImageProperties properties, ParameterProfile profile, String version, HostInfo hostInfo, String additionalParameters);

    String getInstanceLabel(ConnectionRole role);

    String getInstanceVersionLabel(ConnectionRole role);

    void killTlsInstance(TlsInstance tlsInstance);

    void restartInstance(TlsInstance tlsInstance);

    void startInstance(TlsInstance tlsInstance);

    void stopInstance(TlsInstance tlsInstance);

    String getLogsFromTlsInstance(TlsInstance tlsInstance);

}
