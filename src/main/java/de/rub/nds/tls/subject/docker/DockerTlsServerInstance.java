/*
 * TLS-Docker-Library - A collection of open source TLS clients and servers
 *
 * Copyright 2017-2022 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tls.subject.docker;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.NetworkSettings;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports.Binding;
import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.HostInfo;
import de.rub.nds.tls.subject.params.ParameterProfile;
import de.rub.nds.tls.subject.properties.ImageProperties;
import java.util.function.UnaryOperator;

public class DockerTlsServerInstance extends DockerTlsInstance {

    private int port;
    private final HostInfo hostInfo;
    private final String additionalParameters;
    private final boolean parallelize;
    private final boolean insecureConnection;
    private ExposedPort exposedImplementationPort;

    public DockerTlsServerInstance(
            String containerName,
            ParameterProfile profile,
            ImageProperties imageProperties,
            String version,
            boolean autoRemove,
            HostInfo hostInfo,
            String additionalParameters,
            boolean parallelize,
            boolean insecureConnection,
            UnaryOperator<HostConfig> hostConfigHook) {
        super(
                containerName,
                profile,
                imageProperties,
                version,
                ConnectionRole.SERVER,
                autoRemove,
                hostConfigHook);
        this.port = hostInfo.getPort(); // fill with default port
        this.hostInfo = hostInfo;
        this.additionalParameters = additionalParameters;
        this.parallelize = parallelize;
        this.insecureConnection = insecureConnection;
    }

    @Override
    protected HostConfig prepareHostConfig(HostConfig cfg) {
        return super.prepareHostConfig(cfg)
                .withPortBindings(
                        new PortBinding(
                                Binding.empty(),
                                new ExposedPort(
                                        imageProperties.getInternalPort(),
                                        hostInfo.getType().toInternetProtocol())))
                .withReadonlyRootfs(true);
    }

    @Override
    protected CreateContainerCmd prepareCreateContainerCmd(CreateContainerCmd cmd) {
        String host;
        if (hostInfo.getHostname() == null || imageProperties.isUseIP()) {
            host = hostInfo.getIp();
        } else {
            host = hostInfo.getHostname();
        }
        exposedImplementationPort =
                new ExposedPort(hostInfo.getPort(), hostInfo.getType().toInternetProtocol());
        return super.prepareCreateContainerCmd(cmd)
                .withCmd(
                        parameterProfile.toParameters(
                                host,
                                hostInfo.getPort(),
                                imageProperties,
                                additionalParameters,
                                parallelize,
                                insecureConnection))
                .withExposedPorts(exposedImplementationPort);
    }

    @Override
    public void start() {
        super.start();
        updateInstancePort();
    }

    /** Update port to match actually exposed port. */
    public void updateInstancePort() {
        InspectContainerResponse containerInfo = DOCKER.inspectContainerCmd(getId()).exec();
        if (containerInfo == null) {
            throw new IllegalStateException("Could not find container with ID:" + getId());
        }
        NetworkSettings networkSettings = containerInfo.getNetworkSettings();
        if (networkSettings == null) {
            throw new IllegalStateException(
                    "Cannot retrieve InstacePort, Network not properly configured for container with ID:"
                            + getId());
        }
        if (exposedImplementationPort == null) {
            throw new IllegalStateException(
                    "Unable to update port - no exposed port set for container with ID:" + getId());
        }

        Binding[] binding = networkSettings.getPorts().getBindings().get(exposedImplementationPort);
        if (binding != null) {
            // only update if port mapping was necessary
            port = Integer.valueOf(binding[0].getHostPortSpec());
        }
    }

    public int getPort() {
        return port;
    }

    public HostInfo getHostInfo() {
        return hostInfo;
    }
}
