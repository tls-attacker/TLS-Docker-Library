package de.rub.nds.tls.subject.docker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.LogsParam;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.ContainerNotFoundException;
import com.spotify.docker.client.exceptions.DockerException;
import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.HostInfo;
import de.rub.nds.tls.subject.TlsInstance;
import de.rub.nds.tls.subject.TlsInstanceManager;
import de.rub.nds.tls.subject.params.ParameterProfile;
import de.rub.nds.tls.subject.properties.ImageProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Manage a TLS-Instance via Docker
 *
 * One instance is needed for each client or server type. RM TODO: This class is
 * a garbagefire. Please change it!
 */
public class DockerSpotifyTlsInstanceManager implements TlsInstanceManager {

    private static final DockerClient DOCKER = new DefaultDockerClient("unix:///var/run/docker.sock");
    private static final Logger LOGGER = LogManager.getLogger(DockerSpotifyTlsInstanceManager.class);

    private final Map<String, Integer> logReadOffset;

    public DockerSpotifyTlsInstanceManager() {
        logReadOffset = new HashMap<>();
    }

    @Override
    public TlsInstance getTlsInstance(ConnectionRole role, ImageProperties properties, ParameterProfile profile, String version, HostInfo hostInfo, String additionalParameters) {
        return new DockerTlsInstance(role, properties, profile, version, hostInfo, additionalParameters, this);
    }

    @Override
    public void startInstance(TlsInstance tlsInstance) {
        LOGGER.debug("Starting TLS Instance" + tlsInstance.getId());
        try {
            DOCKER.startContainer(tlsInstance.getId());
        } catch (DockerException | InterruptedException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stopInstance(TlsInstance tlsInstance) {
        LOGGER.debug("Stopping TLS Instance" + tlsInstance.getId());
        try {
            DOCKER.stopContainer(tlsInstance.getId(), 2);
        } catch (DockerException | InterruptedException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void restartInstance(TlsInstance tlsInstance) {
        LOGGER.debug("Restarting TLS Instance " + tlsInstance.getId());
        try {
            DOCKER.stopContainer(tlsInstance.getId(), 2);
            DOCKER.startContainer(tlsInstance.getId());
            ((DockerTlsInstance)tlsInstance).updateInstancePort();
        } catch (DockerException | InterruptedException e) {
            LOGGER.error(e);
        }
    }

    @Override
    public void killTlsInstance(TlsInstance tlsInstance) {
        LOGGER.debug("Shutting down TLS Instance " + tlsInstance.getId());
        try {
            DOCKER.stopContainer(tlsInstance.getId(), 2);
            tlsInstance.setExitCode(DOCKER.inspectContainer(tlsInstance.getId()).state().exitCode());
            DOCKER.removeContainer(tlsInstance.getId());
        } catch (DockerException | InterruptedException e) {
            LOGGER.error(e);
        }
    }

    @Override
    public String getLogsFromTlsInstance(TlsInstance tlsInstance) {
        String logs = "-";
        try {
            LogStream logStream = DOCKER.logs(tlsInstance.getId(), LogsParam.stderr(), LogsParam.stdout());
            String[] lines = logStream.readFully().split("\r\n|\r|\n");
            logs = Arrays.stream(lines)
                    .skip(logReadOffset.getOrDefault(tlsInstance.getId(), 0))
                    .map(s -> s.concat("\n"))
                    .reduce(String::concat)
                    .orElse("-");
            logReadOffset.put(tlsInstance.getId(), lines.length);
        } catch (ContainerNotFoundException e) {
            return logs;
        } catch (DockerException | InterruptedException e) {
            LOGGER.error(e);
        }
        return logs;
    }
}
