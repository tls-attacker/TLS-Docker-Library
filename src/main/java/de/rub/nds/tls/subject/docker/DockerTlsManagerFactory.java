package de.rub.nds.tls.subject.docker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Image;
import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.HostInfo;
import de.rub.nds.tls.subject.TlsInstance;
import de.rub.nds.tls.subject.TlsImplementationType;
import de.rub.nds.tls.subject.constants.TransportType;
import de.rub.nds.tls.subject.exceptions.DefaultProfileNotFoundException;
import de.rub.nds.tls.subject.exceptions.ImplementationDidNotStartException;
import de.rub.nds.tls.subject.exceptions.PropertyNotFoundException;
import de.rub.nds.tls.subject.params.ParameterProfile;
import de.rub.nds.tls.subject.params.ParameterProfileManager;
import de.rub.nds.tls.subject.properties.ImageProperties;
import de.rub.nds.tls.subject.properties.PropertyManager;
import java.io.IOException;
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

    private static final DockerClient DOCKER = new DefaultDockerClient("unix:///var/run/docker.sock");
    private static final Logger LOGGER = LogManager.getLogger(DockerTlsManagerFactory.class);

    private static final int DEFAULT_PORT = 4433;
    private final ParameterProfileManager parameterManager;
    private final PropertyManager propertyManager;
    private final DockerSpotifyTlsInstanceManager instanceManager;

    private static final int SERVER_POLL_INTERVAL_MILLISECONDS = 50;

    private static final int TIMEOUT_WAIT_FOR_SERVER_SPINUP_MILLISECONDS = 10000;

    public DockerTlsManagerFactory() {
        parameterManager = new ParameterProfileManager();
        propertyManager = new PropertyManager();
        instanceManager = new DockerSpotifyTlsInstanceManager();
    }

    public TlsInstance getTlsClient(TlsImplementationType type, String version, String ip) {
        return DockerTlsManagerFactory.this.getTlsClient(type, version, ip, null);
    }

    public TlsInstance getTlsClient(TlsImplementationType type, String version, String ip, String hostname) {
        return DockerTlsManagerFactory.this.getTlsClient(type, version, ip, hostname, DEFAULT_PORT);
    }

    public TlsInstance getTlsClient(TlsImplementationType type, String version, String ip, int port) {
        return DockerTlsManagerFactory.this.getTlsClient(type, version, ip, null, port);
    }

    public TlsInstance getTlsClient(TlsImplementationType type, String version, String ip, String hostname, int port) {
        return getTlsClient(type, version, ip, hostname, port, null);
    }

    public TlsInstance getTlsClient(TlsImplementationType type, String version, String ip, int port, String additionalParams) {
        return getTlsClient(type, version, ip, null, port, additionalParams);
    }

    public TlsInstance getTlsClient(TlsImplementationType type, String version, String ip, String hostname, int port, String additionalParams) {
        HostInfo hostInfo = new HostInfo(ip, hostname, port, TransportType.TCP);
        return getInstance(ConnectionRole.CLIENT, type, version, hostInfo, additionalParams);
    }

    public TlsInstance getTlsServer(TlsImplementationType type, String version) {
        return DockerTlsManagerFactory.this.getTlsServer(type, version, DEFAULT_PORT);
    }

    public TlsInstance getTlsServer(TlsImplementationType type, String version, String hostname) {
        return DockerTlsManagerFactory.this.getTlsServer(type, version, hostname, DEFAULT_PORT);
    }

    public TlsInstance getTlsServer(TlsImplementationType type, String version, int port) {
        return DockerTlsManagerFactory.this.getTlsServer(type, version, null, port);
    }

    public TlsInstance getTlsServer(TlsImplementationType type, String version, String hostname, int port) {
        return getTlsServer(type, version, hostname, port, null);
    }

    public TlsInstance getTlsServer(TlsImplementationType type, String version, int port, String additionalParams) {
        return getTlsServer(type, version, null, port, additionalParams);
    }

    public TlsInstance getTlsServer(TlsImplementationType type, String version, String hostname, int port, String additionalParams) {
        HostInfo hostInfo = new HostInfo(hostname, port, TransportType.TCP);
        return getInstance(ConnectionRole.SERVER, type, version, hostInfo, additionalParams);
    }

    public TlsInstance getDtlsClient(TlsImplementationType type, String version, String ip) {
        return DockerTlsManagerFactory.this.getTlsClient(type, version, ip, null);
    }

    public TlsInstance getDtlsClient(TlsImplementationType type, String version, String ip, String hostname) {
        return DockerTlsManagerFactory.this.getTlsClient(type, version, ip, hostname, DEFAULT_PORT);
    }

    public TlsInstance getDtlsClient(TlsImplementationType type, String version, String ip, int port) {
        return DockerTlsManagerFactory.this.getTlsClient(type, version, ip, null, port);
    }

    public TlsInstance getDtlsClient(TlsImplementationType type, String version, String ip, String hostname, int port) {
        return getTlsClient(type, version, ip, hostname, port, null);
    }

    public TlsInstance getDtlsClient(TlsImplementationType type, String version, String ip, int port, String additionalParams) {
        return getTlsClient(type, version, ip, null, port, additionalParams);
    }

    public TlsInstance getDtlsClient(TlsImplementationType type, String version, String ip, String hostname, int port, String additionalParams) {
        HostInfo hostInfo = new HostInfo(ip, hostname, port, TransportType.UDP);
        return getInstance(ConnectionRole.CLIENT, type, version, hostInfo, additionalParams);
    }

    public TlsInstance getDtlsServer(TlsImplementationType type, String version) {
        return DockerTlsManagerFactory.this.getTlsServer(type, version, DEFAULT_PORT);
    }

    public TlsInstance getDtlsServer(TlsImplementationType type, String version, String hostname) {
        return DockerTlsManagerFactory.this.getTlsServer(type, version, hostname, DEFAULT_PORT);
    }

    public TlsInstance getDtlsServer(TlsImplementationType type, String version, int port) {
        return DockerTlsManagerFactory.this.getTlsServer(type, version, null, port);
    }

    public TlsInstance getDtlsServer(TlsImplementationType type, String version, String hostname, int port) {
        return getTlsServer(type, version, hostname, port, null);
    }

    public TlsInstance getDtlsServer(TlsImplementationType type, String version, int port, String additionalParams) {
        return getTlsServer(type, version, null, port, additionalParams);
    }

    public TlsInstance getDtlsServer(TlsImplementationType type, String version, String hostname, int port, String additionalParams) {
        HostInfo hostInfo = new HostInfo(hostname, port, TransportType.UDP);
        return getInstance(ConnectionRole.SERVER, type, version, hostInfo, additionalParams);
    }

    private TlsInstance getInstance(ConnectionRole role, TlsImplementationType type, String version, HostInfo hostInfo, String additionalParams) {
        ParameterProfile profile = retrieveParameterProfile(type, version, role);
        ImageProperties properties = retrieveImageProperties(role, type, version);
        if (hostInfo.getPort() == null) {
            hostInfo.updatePort(properties.getInternalPort());
        }
        return instanceManager.getTlsInstance(role, properties, profile, version, hostInfo, additionalParams);
    }

    private ImageProperties retrieveImageProperties(ConnectionRole role, TlsImplementationType type, String version) throws PropertyNotFoundException {
        ImageProperties properties = propertyManager.getProperties(role, type);
        if (properties == null) {
            throw new PropertyNotFoundException("Could not find a Property for " + role.name() + ": " + type.name() + ":" + version);
        }
        return properties;
    }

    private ParameterProfile retrieveParameterProfile(TlsImplementationType type, String version, ConnectionRole role) throws DefaultProfileNotFoundException {
        ParameterProfile profile = parameterManager.getProfile(type, version, role);
        if (profile == null) {
            throw new DefaultProfileNotFoundException("Could not find a Profile for " + role.name() + ": " + type.name() + ":" + version);
        }
        return profile;
    }

    public void waitUntilServerIsOnline(String host, int port, TlsInstance instance) {
        long startTime = System.currentTimeMillis();
        while (!isServerOnline(host, port)) {
            if (startTime + TIMEOUT_WAIT_FOR_SERVER_SPINUP_MILLISECONDS < System.currentTimeMillis()) {
                throw new ImplementationDidNotStartException("Could not start Server: Timeout");
            }
            try {
                Thread.sleep(SERVER_POLL_INTERVAL_MILLISECONDS);
            } catch (InterruptedException ex) {
                throw new ImplementationDidNotStartException("Interrupted while waiting for Server", ex);
            }
        }
    }

    public boolean isServerOnline(String address, int port) {
        try {
            Socket ss = new Socket(address, port);
            if (ss.isConnected()) {
                ss.close();
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            LOGGER.debug("Server is not online yet", e);
            return false;
        }
    }

    public List<String> getAvailableVersions(ConnectionRole role, TlsImplementationType type) {
        List<String> versionList = new LinkedList<>();
        try {
            List<Image> serverImageList = DOCKER.listImages(DockerClient.ListImagesParam.withLabel(ConnectionRole.getInstanceLabel(role), type.name().toLowerCase()));
            for (Image image : serverImageList) {
                if (image.labels() != null) {
                    String version = image.labels().get(ConnectionRole.getInstanceVersionLabel(role));
                    if (version != null) {
                        versionList.add(version);
                    }
                }
            }
            return versionList;
        } catch (DockerException | InterruptedException ex) {
            throw new RuntimeException("Could not retrieve available " + role.name() + " Versions!", ex);
        }
    }
}
