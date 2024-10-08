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
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.NetworkSettings;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports.Binding;
import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.HostInfo;
import de.rub.nds.tls.subject.params.ParameterProfile;
import de.rub.nds.tls.subject.properties.ImageProperties;
import java.util.List;
import java.util.function.UnaryOperator;

public class DockerTlsServerInstance extends DockerTlsInstance {

    private int port;
    private final HostInfo hostInfo;
    private final String additionalParameters;
    private final boolean parallelize;
    private final boolean insecureConnection;
    private ExposedPort exposedImplementationPort;

    public DockerTlsServerInstance(
            Image image,
            String containerName,
            ParameterProfile profile,
            ImageProperties imageProperties,
            String version,
            String additionalBuildFlags,
            boolean autoRemove,
            HostInfo hostInfo,
            String additionalParameters,
            boolean parallelize,
            boolean insecureConnection,
            UnaryOperator<HostConfig> hostConfigHook,
            String[] cmd,
            List<ExposedPort> exposedPorts) {
        super(
                image,
                containerName,
                profile,
                imageProperties,
                version,
                additionalBuildFlags,
                ConnectionRole.SERVER,
                autoRemove,
                hostConfigHook,
                cmd,
                exposedPorts);
        this.port = hostInfo.getPort(); // fill with default port
        this.hostInfo = hostInfo;
        this.additionalParameters = additionalParameters;
        this.parallelize = parallelize;
        this.insecureConnection = insecureConnection;
    }

    @Override
    protected HostConfig prepareHostConfig(HostConfig cfg) {
        super.prepareHostConfig(cfg);
        if (getContainerExposedPorts() == null) {
            return cfg.withPortBindings(
                    new PortBinding(
                            Binding.empty(),
                            new ExposedPort(
                                    imageProperties.getInternalPort(),
                                    hostInfo.getType().toInternetProtocol())));
        }
        return cfg;
    }

    @Override
    protected CreateContainerCmd prepareCreateContainerCmd(CreateContainerCmd cmd) {
        String host;

        if (hostInfo.getHostname() == null || imageProperties.isUseIP()) {
            host = hostInfo.getIp();
        } else {
            host = hostInfo.getHostname();
        }

        if (getContainerExposedPorts() == null) {
            // only set default port mapping if not managed externally
            exposedImplementationPort =
                    new ExposedPort(hostInfo.getPort(), hostInfo.getType().toInternetProtocol());
            cmd.withExposedPorts(exposedImplementationPort);
        }

        if (getCmd() == null) {
            String[] additionalCmds =
                    parameterProfile.toParameters(
                            host,
                            hostInfo.getPort(),
                            imageProperties,
                            additionalParameters,
                            parallelize,
                            insecureConnection);
            cmd.withCmd(additionalCmds);
        }

        return super.prepareCreateContainerCmd(cmd);
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
