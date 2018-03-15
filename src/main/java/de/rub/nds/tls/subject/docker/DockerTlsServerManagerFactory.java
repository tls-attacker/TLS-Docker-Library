package de.rub.nds.tls.subject.docker;


/**
 * Creates TLS-Server Instances as Docker Container
 * Holds the Config for each TLS-Server
 */

public class DockerTlsServerManagerFactory {

    private DockerTlsServerManagerFactory() {}

    public static DockerSpotifyTlsServerManager get(DockerTlsServerType serverType) {
        return new DockerSpotifyTlsServerManager()
                .setTlsServerNameVersion(serverType.getName(), serverType.getVersion())
                .setInternalPort(serverType.getInternalPort())
                .setStartParameter(serverType.getParams());
    }
}
