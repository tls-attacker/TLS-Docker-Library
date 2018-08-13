package de.rub.nds.tls.subject.docker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.LogsParam;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.ContainerNotFoundException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.Image;
import com.spotify.docker.client.messages.PortBinding;
import com.spotify.docker.client.messages.Volume;
import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.TlsInstance;
import de.rub.nds.tls.subject.TlsInstanceManager;
import de.rub.nds.tls.subject.exceptions.CertVolumeNotFoundException;
import de.rub.nds.tls.subject.exceptions.TlsVersionNotFoundException;
import de.rub.nds.tls.subject.params.Parameter;
import de.rub.nds.tls.subject.params.ParameterProfile;
import de.rub.nds.tls.subject.properties.ImageProperties;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manage a TLS-Instance via Docker
 *
 * One instance is needed for each client or server type.
 */
public class DockerSpotifyTlsInstanceManager implements TlsInstanceManager {

    private static final DockerClient DOCKER = new DefaultDockerClient("unix:///var/run/docker.sock");
    private static final Logger LOGGER = LogManager.getLogger(DockerSpotifyTlsInstanceManager.class);
    private static final String CLIENT_LABEL = "client_type";
    private static final String CLIENT_VERSION_LABEL = "client_version";
    private static final String SERVER_LABEL = "server_type";
    private static final String SERVER_VERSION_LABEL = "server_version";
    private final Map<String, Integer> logReadOffset;

    DockerSpotifyTlsInstanceManager() {
        logReadOffset = new HashMap<>();
    }

    @Override
    public TlsInstance getTlsInstance(ConnectionRole role, ImageProperties properties, ParameterProfile profile, String version, String host, int port, String additionalParameters) {
        try {
            Image image = DOCKER.listImages(DockerClient.ListImagesParam.withLabel(getInstanceLabel(role), profile.getType().name().toLowerCase()), DockerClient.ListImagesParam.withLabel(getInstanceVersionLabel(role), version)).stream()
                    .findFirst()
                    .orElseThrow(() -> new TlsVersionNotFoundException());
            String id = DOCKER.createContainer(
                    ContainerConfig.builder()
                    .image(image.id())
                    .hostConfig(getInstanceHostConfig(role, properties, host))
                    .exposedPorts(properties.getInternalPort() + "/tcp")
                    .attachStderr(true)
                    .attachStdout(true)
                    .attachStdin(true)
                    .tty(true)
                    .stdinOnce(true)
                    .openStdin(true)
                    .cmd(convertProfileToParams(role, properties, profile, host, port, properties.getDefaultKeyPath(), properties.getDefaultCertPath(), additionalParameters))
                    .env("DISPLAY=$DISPLAY")
                    .build(),
                    profile.getType().name() + "_" + RandomStringUtils.randomAlphanumeric(8)
            ).id();
            LOGGER.debug("Starting TLS Instance " + id);
            DOCKER.startContainer(id);
            TlsInstance tlsInstance = new TlsInstance(id, role, host, getInstancePort(role, port, id), profile.getType().name(), this);
            LOGGER.debug(getLogsFromTlsInstance(tlsInstance));
            LOGGER.debug(String.format("Started TLS " + role.name() + " %s : %s(%s)", id, profile.getType().name(), version));
            return tlsInstance;
        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getInstanceLabel(ConnectionRole role) {
        switch (role) {
            case CLIENT:
                return CLIENT_LABEL;
            case SERVER:
                return SERVER_LABEL;
            default:
                throw new IllegalArgumentException("Unknown ConnectionRole: " + role.name());
        }
    }

    @Override
    public String getInstanceVersionLabel(ConnectionRole role) {
        switch (role) {
            case CLIENT:
                return CLIENT_VERSION_LABEL;
            case SERVER:
                return SERVER_VERSION_LABEL;
            default:
                throw new IllegalArgumentException("Unknown ConnectionRole: " + role.name());
        }
    }

    @Override
    public void killTlsInstance(TlsInstance tlsInstance) {
        LOGGER.debug("Shutting down TLS Instance " + tlsInstance.getId());
        try {
            DOCKER.stopContainer(tlsInstance.getId(), 2);
            tlsInstance.setExitCode(DOCKER.inspectContainer(tlsInstance.getId()).state().exitCode());
            DOCKER.removeContainer(tlsInstance.getId());
        } catch (ContainerNotFoundException e) {
            LOGGER.debug(e);
        } catch (DockerException | InterruptedException e) {
            LOGGER.debug(e);
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
            LOGGER.debug(e);
        }
        return logs;
    }

    private HostConfig getInstanceHostConfig(ConnectionRole role, ImageProperties properties, String host) {
        try {
            Volume volume;
            switch (role) {
                case CLIENT:
                    //ToDo: Bind of X11 Settings fails
                    return HostConfig.builder()
                            .binds(HostConfig.Bind.builder()        
                                    .from("/tmp/.X11-unix")
                                    .readOnly(true)
                                    .to("/tmp/.X11-unix")
                                    .build())
                            .build();
                case SERVER:
                    volume = DOCKER.listVolumes(DockerClient.ListVolumesParam.name("cert-data")).volumes().stream()
                            .findFirst()
                            .orElseThrow(() -> new CertVolumeNotFoundException());
                    return HostConfig.builder()
                            .portBindings(Collections.singletonMap(properties.getInternalPort() + "/tcp", Collections.singletonList(PortBinding.randomPort(host))))
                            .binds(HostConfig.Bind.builder()
                                    .from(volume)
                                    .readOnly(true)
                                    .noCopy(true)
                                    .to("/cert/")
                                    .build())
                            .readonlyRootfs(true)
                            .build();
                default:
                    throw new IllegalArgumentException("Unknown ConnectionRole: " + role.name());
            }
        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    

    private int getInstancePort(ConnectionRole role, int port, String id) {
        switch (role) {
            case CLIENT:
                return port;
            case SERVER:
                try {
                    return new Integer(DOCKER.inspectContainer(id).networkSettings().ports().get(port + "/tcp").get(0).hostPort());
                } catch (DockerException | InterruptedException e) {
                    e.printStackTrace();
                }
            default:
                throw new IllegalArgumentException("Unknown ConnectionRole: " + role.name());
        }
    }

    private String[] convertProfileToParams(ConnectionRole role, ImageProperties properties, ParameterProfile profile, String host, int port, String certPath, String keyPath, String additionalParameters) {
        StringBuilder finalParams = new StringBuilder();
        for (Parameter param : profile.getParameterList()) {
            finalParams.append(param.getCmdParameter());
            finalParams.append(" ");
        }
        if (additionalParameters != null) {
            finalParams.append(additionalParameters);
        }
        String afterReplace = finalParams.toString();
        if (host != null) {
            afterReplace = afterReplace.replace("[host]", host);
        }
        if (port != 0) {
            afterReplace = afterReplace.replace("[port]", "" + port);
        }
        if (certPath != null) {
            afterReplace = afterReplace.replace("[cert]", certPath);
        }
        if (keyPath != null) {
            afterReplace = afterReplace.replace("[key]", keyPath);
        }
        LOGGER.debug("Final parameters: " + afterReplace);
        return afterReplace.trim().split(" ");
    }
}
