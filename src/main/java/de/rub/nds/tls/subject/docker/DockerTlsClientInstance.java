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
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.ContainerConfig;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Volume;
import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.HostInfo;
import de.rub.nds.tls.subject.params.ParameterProfile;
import de.rub.nds.tls.subject.properties.ImageProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.UnaryOperator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DockerTlsClientInstance extends DockerTlsInstance {
    private static final String[] EMPTY_STR_ARR = {};
    private static final Logger LOGGER = LogManager.getLogger();

    private final HostInfo hostInfo;
    private final String additionalParameters;
    private final boolean parallelize;
    private final boolean insecureConnection;
    private final boolean connectOnStartup;

    // TODO move away from HostInfo for client...
    public DockerTlsClientInstance(
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
            boolean connectOnStartup,
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
                ConnectionRole.CLIENT,
                autoRemove,
                hostConfigHook,
                cmd,
                exposedPorts);
        this.hostInfo = hostInfo;
        this.additionalParameters = additionalParameters;
        this.parallelize = parallelize;
        this.insecureConnection = insecureConnection;
        this.connectOnStartup = connectOnStartup;
    }

    @Override
    protected HostConfig prepareHostConfig(HostConfig cfg) {
        String extraHost = "test:127.0.0.27";
        if (hostInfo.getHostname() != null) {
            extraHost = hostInfo.getHostname() + ":" + hostInfo.getIp();
        }
        cfg = super.prepareHostConfig(cfg).withExtraHosts(extraHost);

        List<Bind> binds = new ArrayList<>(Arrays.asList(cfg.getBinds()));
        // TODO: Bind of X11 Settings does not work as expected
        binds.add(new Bind("/tmp/.X11-unix", new Volume("/tmp/.X11-unix")));
        cfg = cfg.withBinds(binds);

        return cfg;
    }

    @Override
    protected CreateContainerCmd prepareCreateContainerCmd(CreateContainerCmd cmd) {
        cmd = super.prepareCreateContainerCmd(cmd);

        String host;
        if (hostInfo.getHostname() == null || imageProperties.isUseIP()) {
            host = hostInfo.getIp();
        } else {
            host = hostInfo.getHostname();
        }
        if (connectOnStartup) {
            cmd =
                    cmd.withCmd(
                            parameterProfile.toParameters(
                                    host,
                                    hostInfo.getPort(),
                                    imageProperties,
                                    additionalParameters,
                                    parallelize,
                                    insecureConnection));
        } else {
            cmd = cmd.withEntrypoint("client-entrypoint");
        }
        return cmd;
    }

    public DockerExecInstance connect() {
        String host;
        if (hostInfo.getHostname() == null || imageProperties.isUseIP()) {
            host = hostInfo.getIp();
        } else {
            host = hostInfo.getHostname();
        }
        return connect(host, hostInfo.getPort());
    }

    public DockerExecInstance connect(String host, int targetPort) {
        return connect(host, targetPort, additionalParameters, parallelize, insecureConnection);
    }

    public DockerExecInstance connect(String host, int targetPort, String additionalParameters) {
        return connect(host, targetPort, additionalParameters, parallelize, insecureConnection);
    }

    public DockerExecInstance connect(
            String host,
            int targetPort,
            String additionalParameters,
            Boolean parallelize,
            Boolean insecureConnection) {
        ContainerConfig imageCfg = DOCKER.inspectImageCmd(image.getId()).exec().getConfig();
        if (imageCfg == null) {
            throw new IllegalStateException("Could not get config for image " + image.getId());
        }
        String[] entrypoint = imageCfg.getEntrypoint();
        if (entrypoint == null) {
            throw new IllegalStateException("Could not get entrypoint for image " + image.getId());
        }
        List<String> cmd_lst = new LinkedList<String>(Arrays.asList(entrypoint));
        if (cmd_lst.get(0).equals("client-entrypoint")) {
            cmd_lst.remove(0);
        } else {
            LOGGER.warn("Image {} did not have client-entrypoint as entrypoint", image.getId());
        }
        String[] params =
                parameterProfile.toParameters(
                        host,
                        targetPort,
                        imageProperties,
                        additionalParameters,
                        parallelize,
                        insecureConnection);
        cmd_lst.addAll(Arrays.asList(params));
        ExecCreateCmdResponse exec =
                DOCKER.execCreateCmd(getId())
                        .withCmd(cmd_lst.toArray(EMPTY_STR_ARR))
                        .withAttachStdin(false)
                        .withAttachStdout(true)
                        .withAttachStderr(true)
                        .withTty(true)
                        .exec();
        DockerExecInstance ret = new DockerExecInstance(exec);
        childExecs.add(ret);
        return ret;
    }
}
