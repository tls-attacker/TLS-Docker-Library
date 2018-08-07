package de.rub.nds.tls.subject.docker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Image;
import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.TlsClient;
import de.rub.nds.tls.subject.TlsImplementationType;
import de.rub.nds.tls.subject.TlsServer;
import de.rub.nds.tls.subject.exceptions.DefaultProfileNotFoundException;
import de.rub.nds.tls.subject.exceptions.ImplementationDidNotStartException;
import de.rub.nds.tls.subject.exceptions.PropertyNotFoundException;
import de.rub.nds.tls.subject.params.Parameter;
import de.rub.nds.tls.subject.params.ParameterProfile;
import de.rub.nds.tls.subject.params.ParameterProfileManager;
import de.rub.nds.tls.subject.params.ParameterType;
import de.rub.nds.tls.subject.properties.ClientImageProperties;
import de.rub.nds.tls.subject.properties.ClientPropertyManager;
import de.rub.nds.tls.subject.properties.ServerImageProperties;
import de.rub.nds.tls.subject.properties.ServerPropertyManager;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Creates TLS-Server or TLS-Client Instances as Docker Container Holds the
 * Config for each TLS-Server or TLS-Client
 */
public class DockerTlsManagerFactory {

    private static final DockerClient docker = new DefaultDockerClient("unix:///var/run/docker.sock");

    private static final Logger LOGGER = LogManager.getLogger(DockerTlsManagerFactory.class);

    private static final int DEFAULT_PORT = 443;

    private final ParameterProfileManager parameterManager;
    private final ServerPropertyManager serverPropertyManager;
    private final ClientPropertyManager clientPropertyManager;
    private final DockerSpotifyTlsServerManager serverManager;
    private final DockerSpotifyTlsClientManager clientManager;

    public DockerTlsManagerFactory() {
        parameterManager = new ParameterProfileManager();
        serverPropertyManager = new ServerPropertyManager();
        clientPropertyManager = new ClientPropertyManager();
        clientManager = new DockerSpotifyTlsClientManager();
        serverManager = new DockerSpotifyTlsServerManager();
    }

    private static final String SERVER_LABEL = "server_type";
    private static final String SERVER_VERSION_LABEL = "server_version";

    private static final String CLIENT_LABEL = "client_type";
    private static final String CLIENT_VERSION_LABEL = "client_version";

    public TlsServer getServer(TlsImplementationType type, String version) {
        return getServer(type, version, null);
    }

    public TlsClient getClient(TlsImplementationType type, String version, String host) {
        return getClient(type, version, host, DEFAULT_PORT, null);
    }

    public TlsClient getClient(TlsImplementationType type, String version, String host, int port) {
        return getClient(type, version, host, port, null);
    }

    public TlsServer getServer(TlsImplementationType type, String version, ParameterProfile profile, String additionalParams) {
        ServerImageProperties defaultProperties = serverPropertyManager.getProperties(type);
        if (defaultProperties == null) {
            throw new PropertyNotFoundException("Could not find a default Property for server: " + type.name() + ":" + version);
        }
        TlsServer server = serverManager.getTlsServer(defaultProperties, profile, version, additionalParams);
        long startTime = System.currentTimeMillis();
        while (!isServerOnline(server.getHost(), server.getPort())) {
            if (startTime + 10000 < System.currentTimeMillis()) {
                throw new ImplementationDidNotStartException("Timeout");
            }
            try {
                Thread.currentThread().sleep(50);
            } catch (InterruptedException ex) {
                throw new ImplementationDidNotStartException("Interrupted while waiting for server", ex);
            }
        }
        return server;
    }

    public TlsServer getServer(TlsImplementationType type, String version, String additionalParams) {
        ParameterProfile profile = parameterManager.getProfile(type, version, ConnectionRole.SERVER);
        if (profile == null) {
            throw new DefaultProfileNotFoundException("Could not find a Profile for server: " + type.name() + ":" + version);
        }
        return getServer(type, version, profile, additionalParams);
    }

    public TlsClient getClient(TlsImplementationType type, String version, String host, int port, String additionalParams) {
        ParameterProfile profile = parameterManager.getProfile(type, version, ConnectionRole.CLIENT);
        if (profile == null) {
            throw new DefaultProfileNotFoundException("Could not find a Profile for server: " + type.name() + ":" + version);
        }
        ClientImageProperties defaultProperties = clientPropertyManager.getProperties(type);
        if (defaultProperties == null) {
            throw new PropertyNotFoundException("Could not find a default Property for client: " + type.name() + ":" + version);
        }
        TlsClient client = clientManager.getTlsClient(defaultProperties, profile, host, port);
        return client;
    }

    public boolean isServerOnline(String address, int port) {
        try {
            LOGGER.debug("Testing if server is online...");
            InetSocketAddress sa = new InetSocketAddress(address, port);
            Socket ss = new Socket();
            ss.connect(sa);
            ss.close();
            LOGGER.debug("Server is ready!");
            return true;
        } catch (IOException e) {
            LOGGER.debug("Server is not online yet");
            LOGGER.trace(e);
            return false;
        }
    }

    public List<String> getAvailableServerVersions(TlsImplementationType implementationType) {
        List<String> serverVersionList = new LinkedList<>();
        try {
            List<Image> serverImageList = docker.listImages(DockerClient.ListImagesParam.withLabel(SERVER_LABEL, implementationType.name().toLowerCase()));
            for (Image image : serverImageList) {
                if (image.labels() != null) {
                    String version = image.labels().get(SERVER_VERSION_LABEL);
                    if (version != null) {
                        serverVersionList.add(version);
                    }
                }
            }
            return serverVersionList;
        } catch (DockerException | InterruptedException ex) {
            throw new RuntimeException("Could not retrieve available Versions!");
        }
    }

    public List<String> getAvailableClientVersions(TlsImplementationType implementationType) {
        List<String> clientVersionList = new LinkedList<>();
        try {
            List<Image> clientImageList = docker.listImages(DockerClient.ListImagesParam.withLabel(CLIENT_LABEL, implementationType.name().toLowerCase()));
            for (Image image : clientImageList) {
                if (image.labels() != null) {
                    String version = image.labels().get(CLIENT_VERSION_LABEL);
                    if (version != null) {
                        clientVersionList.add(version);
                    }
                }
            }
            return clientVersionList;
        } catch (DockerException | InterruptedException ex) {
            throw new RuntimeException("Could not retrieve available Versions!");
        }
    }
}
