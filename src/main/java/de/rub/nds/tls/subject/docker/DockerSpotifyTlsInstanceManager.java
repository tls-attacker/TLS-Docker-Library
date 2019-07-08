package de.rub.nds.tls.subject.docker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.LogsParam;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.ContainerNotFoundException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.HostConfig.Bind;
import com.spotify.docker.client.messages.Image;
import com.spotify.docker.client.messages.PortBinding;
import com.spotify.docker.client.messages.Volume;
import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.HostInfo;
import de.rub.nds.tls.subject.TlsInstance;
import de.rub.nds.tls.subject.TlsInstanceManager;
import de.rub.nds.tls.subject.exceptions.CertVolumeNotFoundException;
import de.rub.nds.tls.subject.exceptions.ImplementationDidNotStartException;
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
    public TlsInstance getTlsInstance(ConnectionRole role, ImageProperties properties, ParameterProfile profile, String version, HostInfo hostInfo, String additionalParameters) {
        String host = getIpOrHostNameToUse(hostInfo, properties);
        try {
            Image image = DOCKER.listImages(DockerClient.ListImagesParam.withLabel(getInstanceLabel(role), profile.getType().name().toLowerCase()), DockerClient.ListImagesParam.withLabel(getInstanceVersionLabel(role), version)).stream()
                    .findFirst()
                    .orElseThrow(() -> new TlsVersionNotFoundException());
            String id = DOCKER.createContainer(
                    ContainerConfig.builder()
                            .image(image.id())
                            .hostConfig(getInstanceHostConfig(role, properties, hostInfo))
                            .exposedPorts(properties.getInternalPort() + "/tcp")
                            .exposedPorts(properties.getInternalPort() + "/udp")
                            .attachStderr(true)
                            .attachStdout(true)
                            .attachStdin(true)
                            .tty(true)
                            .stdinOnce(true)
                            .openStdin(true)
                            .cmd(convertProfileToParams(profile, host, hostInfo.getPort(), properties, additionalParameters))
                            //.env("DISPLAY=$DISPLAY")
                            .build(),
                    profile.getType().name() + "_" + RandomStringUtils.randomAlphanumeric(8)
            ).id();
            if (role == ConnectionRole.CLIENT) {
                return new TlsInstance(id, role, host, getInstancePort(role, hostInfo.getPort(), id), profile.getType().name(), this, hostInfo);
            } else {
                return new TlsInstance(id, role, host, null, profile.getType().name(), this, hostInfo);
            }

        } catch (DockerException | InterruptedException e) {
            LOGGER.error("Could not create instance");
            throw new ImplementationDidNotStartException("Could not create instance");
        }
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
    public void startInstance(TlsInstance tlsInstance) {
        LOGGER.debug("Starting TLS Instance" + tlsInstance.getId());
        try {
            DOCKER.startContainer(tlsInstance.getId());
            tlsInstance.setPort(getInstancePort(tlsInstance.getConnectionRole(), tlsInstance.getHostInfo().getPort(), tlsInstance.getId()));
        } catch (ContainerNotFoundException e) {
            LOGGER.debug(e);
        } catch (DockerException | InterruptedException e) {
            LOGGER.debug(e);
        }
    }

    @Override
    public void stopInstance(TlsInstance tlsInstance) {
        LOGGER.debug("Stopping TLS Instance" + tlsInstance.getId());
        try {
            DOCKER.stopContainer(tlsInstance.getId(), 2);
        } catch (ContainerNotFoundException e) {
            LOGGER.debug(e);
        } catch (DockerException | InterruptedException e) {
            LOGGER.debug(e);
        }
    }

    @Override
    public void restartInstance(TlsInstance tlsInstance) {
        LOGGER.debug("Restarting TLS Instance " + tlsInstance.getId());
        try {
            DOCKER.stopContainer(tlsInstance.getId(), 2);
            DOCKER.startContainer(tlsInstance.getId());
        } catch (ContainerNotFoundException e) {
            LOGGER.debug(e);
        } catch (DockerException | InterruptedException e) {
            LOGGER.debug(e);
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

    private String getIpOrHostNameToUse(HostInfo hostInfo, ImageProperties properties) {
        String host;
        if (hostInfo.getHostname() == null || properties.isUseIP()) {
            host = hostInfo.getIp();
        } else {
            host = hostInfo.getHostname();
        }
        return host;
    }

    private HostConfig getInstanceHostConfig(ConnectionRole role, ImageProperties properties, HostInfo hostInfo) {
        try {
            Volume volume;
            switch (role) {
                case CLIENT:
                    String extraHost = "test:127.0.0.27";
                    if (hostInfo.getHostname() != null) {
                        extraHost = hostInfo.getHostname() + ":" + hostInfo.getIp();
                    }
                    volume = DOCKER.listVolumes(DockerClient.ListVolumesParam.name("cert-data")).volumes().stream()
                            .findFirst()
                            .orElseThrow(() -> new CertVolumeNotFoundException());
                    return HostConfig.builder()
                            .extraHosts(extraHost)
                            .appendBinds(Bind.from(volume)
                                    .to("/cert/")
                                    .readOnly(true)
                                    .noCopy(true)
                                    .build())
                            //ToDo: Bind of X11 Settings does not work as expected
                            .appendBinds(Bind.from("/tmp/.X11-unix")
                                    .to("/tmp/.X11-unix")
                                    .build())
                            .build();
                case SERVER:
                    volume = DOCKER.listVolumes(DockerClient.ListVolumesParam.name("cert-data")).volumes().stream()
                            .findFirst()
                            .orElseThrow(() -> new CertVolumeNotFoundException());
                    return HostConfig.builder()
                            .portBindings(Collections.singletonMap(properties.getInternalPort() + "/tcp", Collections.singletonList(PortBinding.randomPort(hostInfo.getHostname()))))
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
            LOGGER.error("Could not get host config", e);
            throw new RuntimeException("Cannot create HostConfig", e);
        }
    }

    private int getInstancePort(ConnectionRole role, int port, String id) {
        switch (role) {
            case CLIENT:
                return port;
            case SERVER:
                try {
                    return new Integer(DOCKER.inspectContainer(id).networkSettings().ports().get(port + "/tcp").get(0).hostPort());
                } catch (DockerException | InterruptedException e) {
                    LOGGER.error("Could not retrieve instance port", e);
                }
            default:
                throw new IllegalArgumentException("Unknown ConnectionRole: " + role.name());
        }
    }

    private String[] convertProfileToParams(ParameterProfile profile, String host, Integer port, ImageProperties properties, String additionalParameters) {
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
        if (port != null) {
            afterReplace = afterReplace.replace("[port]", "" + port);
        }
        if (properties.getDefaultCertPath() != null) {
            afterReplace = afterReplace.replace("[cert]", properties.getDefaultCertPath());
        }
        if (properties.getDefaultKeyPath() != null) {
            afterReplace = afterReplace.replace("[key]", properties.getDefaultKeyPath());
        }
        afterReplace = afterReplace.trim();
        LOGGER.debug("Final parameters: " + (afterReplace));
        return afterReplace.split(" ");
    }
}
