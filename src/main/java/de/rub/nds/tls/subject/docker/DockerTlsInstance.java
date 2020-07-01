package de.rub.nds.tls.subject.docker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.Image;
import com.spotify.docker.client.messages.NetworkSettings;
import com.spotify.docker.client.messages.PortBinding;
import com.spotify.docker.client.messages.Volume;
import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.HostInfo;
import de.rub.nds.tls.subject.TlsInstance;
import de.rub.nds.tls.subject.constants.TlsImageLabels;
import de.rub.nds.tls.subject.constants.TransportType;
import de.rub.nds.tls.subject.exceptions.CertVolumeNotFoundException;
import de.rub.nds.tls.subject.exceptions.TlsVersionNotFoundException;
import de.rub.nds.tls.subject.params.Parameter;
import de.rub.nds.tls.subject.params.ParameterProfile;
import de.rub.nds.tls.subject.params.ParameterType;
import de.rub.nds.tls.subject.properties.ImageProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * The representation of a TLS-Instance used for a Test
 */
public class DockerTlsInstance implements TlsInstance {
    private static final DockerClient DOCKER = new DefaultDockerClient("unix:///var/run/docker.sock");
    private static final Logger LOGGER = LogManager.getLogger();

    private final DockerSpotifyTlsInstanceManager tlsInstanceManager;

    private final ConnectionRole role;
    private String host;
    private Integer port;
    private final HostInfo hostInfo;

    private Image image;
    private String additionalParameters;
    private ImageProperties imageProperties;

    private ParameterProfile parameterProfile;
    private String containerId;
    private String name;
    private ContainerConfig containerConfig;
    private long exitCode;

    private boolean insecureConnection;

    public DockerTlsInstance(ConnectionRole role, ImageProperties properties, ParameterProfile profile, String version, HostInfo hostInfo, String additionalParameters, DockerSpotifyTlsInstanceManager instance) {
        this.role = role;
        this.hostInfo = hostInfo;
        this.imageProperties = properties;
        this.parameterProfile = profile;
        this.tlsInstanceManager = instance;
        this.additionalParameters = additionalParameters;
        this.port = hostInfo.getPort();

        try {
            setImage(DOCKER.listImages(
                    DockerClient.ListImagesParam.withLabel(TlsImageLabels.LIBRARY.getLabelName(), profile.getType().name().toLowerCase()),
                    DockerClient.ListImagesParam.withLabel(TlsImageLabels.VERSION.getLabelName(), version),
                    DockerClient.ListImagesParam.withLabel(TlsImageLabels.MODE.getLabelName(), role.toString().toLowerCase())
            )
                    .stream()
                    .findFirst()
                    .orElseThrow(TlsVersionNotFoundException::new));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public HostInfo getHostInfo() {
        return hostInfo;
    }

    public String getId() {
        if (containerId == null) {
            createContainer();
        }
        return containerId;
    }

    public ConnectionRole getConnectionRole() {
        return role;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public String getLogs() {
        return tlsInstanceManager.getLogsFromTlsInstance(this);
    }

    public long getExitCode() {
        return exitCode;
    }

    public String getExitInfo() {
        return "exitCode: " + exitCode;
    }

    public void setExitCode(long exitCode) {
        this.exitCode = exitCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isInsecureConnection() {
        return insecureConnection;
    }

    public void setInsecureConnection(boolean insecureConnection) {
        this.insecureConnection = insecureConnection;
    }


    public String getAdditionalParameters() {
        return additionalParameters;
    }

    public void setAdditionalParameters(String additionalParameters) {
        this.additionalParameters = additionalParameters;
    }

    public ImageProperties getImageProperties() {
        return imageProperties;
    }

    public void setImageProperties(ImageProperties imageProperties) {
        this.imageProperties = imageProperties;
    }

    public ParameterProfile getParameterProfile() {
        return parameterProfile;
    }

    public void setParameterProfile(ParameterProfile parameterProfile) {
        this.parameterProfile = parameterProfile;
    }

    public void kill() {
        tlsInstanceManager.killTlsInstance(this);
    }

    public void restart() {
        tlsInstanceManager.restartInstance(this);
    }



    @Override
    public String toString() {
        return String.format("%s: %s:%d (%s)", getConnectionRole().name(), host, port, getName());
    }

    public void createContainer() {
        if (this.containerId != null)
            return;
        if (this.image == null)
            throw new RuntimeException("Container could not be created, image is missing");

        try {
            if (containerConfig == null)
                containerConfig = generateContainerConfig();

            this.containerId = DOCKER.createContainer(containerConfig, this.name).id();
        } catch (Exception e) {
            throw new RuntimeException("Container could not be created", e);
        }
    }

    public void start() {
        if (insecureConnection && !parameterProfile.supportsInsecure() && role == ConnectionRole.CLIENT) {
            LOGGER.warn(this.getName() + " does not support insecure connection");
            return;
        }
        createContainer();
        tlsInstanceManager.startInstance(this);
        updateInstancePort();
    }

    public void stop() {
        tlsInstanceManager.stopInstance(this);
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        ImmutableList<String> tags = image.repoTags();
        if (tags != null && tags.size() > 0)
            this.name = tags.get(0);
        this.image = image;
    }

    public ContainerConfig getContainerConfig() {
        if (containerConfig == null)
            containerConfig = generateContainerConfig();
        return containerConfig;
    }

    public void setContainerConfig(ContainerConfig containerConfig) {
        this.containerConfig = containerConfig;
    }

    public ContainerConfig generateContainerConfig() {
        String protocol = hostInfo.getType() == TransportType.TCP ? "/tcp" : "/udp";

        Integer targetPort = imageProperties.getInternalPort();
        if (role == ConnectionRole.CLIENT) {
            targetPort = hostInfo.getPort();
        }

        if (hostInfo.getHostname() == null || imageProperties.isUseIP()) {
            host = hostInfo.getIp();
        } else {
            host = hostInfo.getHostname();
        }

        return ContainerConfig.builder()
                .image(image.id())
                .hostConfig(getInstanceHostConfig())
                .exposedPorts(imageProperties.getInternalPort() + protocol)
                .attachStderr(true)
                .attachStdout(true)
                .attachStdin(true)
                .tty(true)
                .stdinOnce(true)
                .openStdin(true)
                .cmd(convertProfileToParams(targetPort))
                //.env("DISPLAY=$DISPLAY"));
                .build();
    }

    private HostConfig getInstanceHostConfig() {
        try {
            Volume volume = DOCKER.listVolumes(DockerClient.ListVolumesParam.name("cert-data")).volumes().stream()
                    .findFirst()
                    .orElseThrow(CertVolumeNotFoundException::new);

            switch (role) {
                case CLIENT:
                    String extraHost = "test:127.0.0.27";
                    if (hostInfo.getHostname() != null) {
                        extraHost = hostInfo.getHostname() + ":" + hostInfo.getIp();
                    }

                    return HostConfig.builder()
                            .extraHosts(extraHost)
                            .appendBinds(HostConfig.Bind.from(volume)
                                    .to("/cert/")
                                    .readOnly(true)
                                    .noCopy(true)
                                    .build())
                            //ToDo: Bind of X11 Settings does not work as expected
                            .appendBinds(HostConfig.Bind.from("/tmp/.X11-unix")
                                    .to("/tmp/.X11-unix")
                                    .build())
                            .build();
                case SERVER:
                    String protocol = hostInfo.getType() == TransportType.TCP ? "/tcp" : "/udp";
                    return HostConfig.builder()
                            .portBindings(ImmutableMap.of(imageProperties.getInternalPort() + protocol, Arrays.asList(PortBinding.of("127.0.0.42", "" + hostInfo.getPort()))))
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
        } catch (Exception e) {
            LOGGER.error("Could not get host config", e);
            throw new RuntimeException("Cannot create HostConfig", e);
        }
    }

    public void updateInstancePort() {
        switch (role) {
            case CLIENT:
                port = hostInfo.getPort();
                break;
            case SERVER:
                try {
                    ContainerInfo containerInfo = DOCKER.inspectContainer(containerId);
                    if (containerInfo == null) {
                        throw new DockerException("Could not find container with ID:" + containerId);
                    }
                    NetworkSettings networkSettings = containerInfo.networkSettings();
                    if (networkSettings == null) {
                        throw new DockerException("Cannot retrieve InstacePort, Network not properly configured for container with ID:" + containerId);
                    }

                    port = new Integer(networkSettings.ports().values().asList().get(0).get(0).hostPort());
                } catch (Exception e) {
                    LOGGER.error("Could not retrieve instance port", e);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown ConnectionRole: " + role.name());
        }
    }

    private String[] convertProfileToParams(Integer targetPort) {
        StringBuilder finalParams = new StringBuilder();
        for (Parameter param : parameterProfile.getParameterList()) {
            if (insecureConnection && parameterProfile.supportsInsecure()) {
                if (param.getType() == ParameterType.CA_CERTIFICATE) continue;
            } else if (parameterProfile.supportsInsecure()){
                if (param.getType() == ParameterType.INSECURE) continue;
            }
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
        if (targetPort != null) {
            afterReplace = afterReplace.replace("[port]", "" + targetPort);
        }
        if (imageProperties.getDefaultCertPath() != null) {
            afterReplace = afterReplace.replace("[cert]", imageProperties.getDefaultCertPath());
        }
        if (imageProperties.getDefaultKeyPath() != null) {
            afterReplace = afterReplace.replace("[key]", imageProperties.getDefaultKeyPath());
        }
        afterReplace = afterReplace.trim();
        LOGGER.debug("Final parameters: " + (afterReplace));
        return afterReplace.split(" ");
    }
}
