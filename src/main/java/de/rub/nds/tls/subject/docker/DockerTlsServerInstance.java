package de.rub.nds.tls.subject.docker;

import java.util.Arrays;

import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.NetworkSettings;
import com.spotify.docker.client.messages.PortBinding;

import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.HostInfo;
import de.rub.nds.tls.subject.constants.TransportType;
import de.rub.nds.tls.subject.instance.TlsServerInstance;
import de.rub.nds.tls.subject.params.ParameterProfile;
import de.rub.nds.tls.subject.properties.ImageProperties;
import jersey.repackaged.com.google.common.collect.ImmutableMap;

public class DockerTlsServerInstance extends DockerTlsInstance implements TlsServerInstance {

    private int port;
    private final HostInfo hostInfo;
    private final String additionalParameters;
    private final boolean parallelize;
    private final boolean insecureConnection;

    public DockerTlsServerInstance(ParameterProfile profile, ImageProperties imageProperties, String version, boolean autoRemove, HostInfo hostInfo, String additionalParameters, boolean parallelize,
            boolean insecureConnection)
            throws DockerException, InterruptedException {
        super(profile, imageProperties, version, ConnectionRole.SERVER, autoRemove);
        this.port = hostInfo.getPort(); // fill with default port
        this.hostInfo = hostInfo;
        this.additionalParameters = additionalParameters;
        this.parallelize = parallelize;
        this.insecureConnection = insecureConnection;
    }

    @Override
    protected HostConfig.Builder createHostConfig(HostConfig.Builder builder) throws DockerException, InterruptedException {
        String protocol = hostInfo.getType() == TransportType.TCP ? "/tcp" : "/udp";
        return super.createHostConfig(builder)
                .portBindings(ImmutableMap.of(imageProperties.getInternalPort() + protocol,
                        Arrays.asList(PortBinding.randomPort(""))))
                .readonlyRootfs(true);
    }

    @Override
    protected ContainerConfig.Builder createContainerConfig(ContainerConfig.Builder builder) throws DockerException, InterruptedException {
        String protocol = hostInfo.getType() == TransportType.TCP ? "/tcp" : "/udp";
        String host;
        if (hostInfo.getHostname() == null || imageProperties.isUseIP()) {
            host = hostInfo.getIp();
        } else {
            host = hostInfo.getHostname();
        }

        // TODO we might be interested in exposing the server-entrypoint server
        return super.createContainerConfig(builder)
                .cmd(parameterProfile.toParameters(host, imageProperties.getInternalPort(), imageProperties, additionalParameters, parallelize, insecureConnection))
                .exposedPorts(imageProperties.getInternalPort() + protocol);
    }

    @Override
    public void start() throws DockerException, InterruptedException {
        super.start();
        updateInstancePort();
    }

    /**
     * Update port to match actually exposed port.
     */
    protected void updateInstancePort() throws DockerException, InterruptedException {
        ContainerInfo containerInfo = DOCKER.inspectContainer(getId());
        if (containerInfo == null) {
            throw new DockerException("Could not find container with ID:" + getId());
        }
        NetworkSettings networkSettings = containerInfo.networkSettings();
        if (networkSettings == null) {
            throw new DockerException("Cannot retrieve InstacePort, Network not properly configured for container with ID:" + getId());
        }
        // TODO: ignore other exposed ports
        port = new Integer(networkSettings.ports().values().asList().get(0).get(0).hostPort());
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public HostInfo getHostInfo() {
        return hostInfo;
    }
}