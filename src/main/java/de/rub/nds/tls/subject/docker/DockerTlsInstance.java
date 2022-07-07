/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tls.subject.docker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse.ContainerState;
import com.github.dockerjava.api.command.InspectVolumeResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.AccessMode;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.SELContext;
import com.github.dockerjava.api.model.Volume;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.constants.TlsImageLabels;
import de.rub.nds.tls.subject.exceptions.CertVolumeNotFoundException;
import de.rub.nds.tls.subject.exceptions.TlsVersionNotFoundException;
import de.rub.nds.tls.subject.params.ParameterProfile;
import de.rub.nds.tls.subject.properties.ImageProperties;

public abstract class DockerTlsInstance {
    protected static final DockerClient DOCKER = DockerClientManager.getDockerClient();
    private static final Logger LOGGER = LogManager.getLogger();

    private final String containerName;
    private String containerId;
    protected final Image image;
    private Optional<Long> exitCode = Optional.empty();
    private boolean autoRemove;
    private int logReadOffset = 0;
    protected final ParameterProfile parameterProfile;
    protected final ImageProperties imageProperties;
    protected List<DockerExecInstance> childExecs = new LinkedList<>();
    private final UnaryOperator<HostConfig> hostConfigHook;

    public DockerTlsInstance(String containerName, ParameterProfile profile, ImageProperties imageProperties,
        String version, ConnectionRole role, boolean autoRemove, UnaryOperator<HostConfig> hostConfigHook) {
        if (profile == null) {
            throw new NullPointerException("profile may not be null");
        }
        if (imageProperties == null) {
            throw new NullPointerException("imageProperties may not be null");
        }
        this.autoRemove = autoRemove;
        this.parameterProfile = profile;
        this.imageProperties = imageProperties;
        this.hostConfigHook = hostConfigHook;
        this.containerName = containerName;
        Map<String, String> labels = new HashMap<>();
        labels.put(TlsImageLabels.IMPLEMENTATION.getLabelName(), profile.getType().name().toLowerCase());
        labels.put(TlsImageLabels.VERSION.getLabelName(), version);
        labels.put(TlsImageLabels.CONNECTION_ROLE.getLabelName(), role.toString().toLowerCase());
        this.image = DOCKER.listImagesCmd().withLabelFilter(labels).exec().stream().findFirst()
            .orElseThrow(TlsVersionNotFoundException::new);
    }

    protected HostConfig prepareHostConfig(HostConfig cfg) {
        // Check if volume exists; Without this check, the container would be started
        // without any problems, swallowing the error and making it harder to identify
        InspectVolumeResponse vol = DOCKER.listVolumesCmd().withFilter("name", Arrays.asList("cert-data")).exec()
            .getVolumes().stream().findFirst().orElseThrow(CertVolumeNotFoundException::new);

        // hook is handled in prepareCreateContainerCmd; this ensures it is called last
        return cfg.withBinds(new Bind(vol.getName(), new Volume("/cert/"), AccessMode.ro, SELContext.DEFAULT, true));
    }

    protected CreateContainerCmd prepareCreateContainerCmd(CreateContainerCmd cmd) {
        HostConfig hcfg = prepareHostConfig(HostConfig.newHostConfig());
        if (hostConfigHook != null) {
            hcfg = hostConfigHook.apply(hcfg);
        }
        return cmd.withAttachStderr(true).withAttachStdout(true).withAttachStdin(true).withTty(true).withStdInOnce(true)
            .withStdinOpen(true).withHostConfig(hcfg);
        // missing: hostConfig, exposedPorts, cmd
    }

    protected String createContainer() {
        if (this.image == null) {
            throw new IllegalStateException("Container could not be created, image is missing");
        }
        @SuppressWarnings("squid:S2095") // sonarlint: Resources should be closed
        // Create container does not need to be closed
        CreateContainerCmd containerCmd = DOCKER.createContainerCmd(image.getId());
        if (containerName != null) {
            containerCmd.withName(containerName);
        }
        containerCmd = prepareCreateContainerCmd(containerCmd);
        CreateContainerResponse container = containerCmd.exec();
        String[] warnings = container.getWarnings();
        if (warnings != null && warnings.length != 0 && LOGGER.isWarnEnabled()) {
            LOGGER.warn("During container creation the following warnings were raised:");
            for (String warning : warnings) {
                LOGGER.warn(warning);
            }
        }
        return container.getId();
    }

    public void ensureContainerExists() {
        // TODO check if container already exists
        if (containerId != null) {
            // check if still exists
            // TODO
        }
        if (containerId == null) {
            // create new container
            containerId = createContainer();
        }
    }

    public void start() {
        ensureContainerExists();
        DOCKER.startContainerCmd(getId()).exec();
    }

    public void remove() {
        String id = getId();
        if (id != null) {
            DOCKER.removeContainerCmd(id).exec();
        }
        closeChildren();
        containerId = null;
    }

    private void autoRemove() {
        if (autoRemove) {
            remove();
        }
    }

    private void storeExitCode() {
        this.exitCode = Optional.of(DOCKER.inspectContainerCmd(getId()).exec().getState().getExitCodeLong());
    }

    private void closeChildren() {
        for (DockerExecInstance exec : childExecs) {
            try {
                exec.close();
            } catch (Exception e) {
                LOGGER.warn("Error while closing exec instance", e);
            }
        }
        childExecs.clear();
    }

    public void stop(int secondsToWaitBeforeKilling) {
        DOCKER.stopContainerCmd(getId()).withTimeout(secondsToWaitBeforeKilling).exec();
        closeChildren();
        storeExitCode();
        autoRemove();
    }

    public void stop() {
        stop(2);
    }

    public void kill() {
        DOCKER.killContainerCmd(getId()).exec();
        closeChildren();
        storeExitCode();
        autoRemove();
    }

    public Image getImage() {
        return image;
    }

    @SuppressWarnings("squid:S2142") // sonarlint: "InterruptedException" should not be ignored
    // we rethrow the interrupted exception a bit later
    public void close() {
        closeChildren();
        if (autoRemove) {
            try {
                String id = getId();
                if (id != null) {
                    DOCKER.killContainerCmd(id).exec();
                }
            } catch (DockerException e) {
                LOGGER.warn("Failed to kill container on close()");
            }
            try {
                remove();
            } catch (DockerException e) {
                // we did our best
                LOGGER.warn("Failed to remove container on close()", e);
            }
        }
    }

    public void restart() {
        DOCKER.restartContainerCmd(getId()).exec();
    }

    public String getId() {
        return containerId;
    }

    public String getLogs() throws InterruptedException {
        FrameHandler fh = new FrameHandler();
        DOCKER.logContainerCmd(getId()).exec(fh);
        fh.awaitCompletion();
        String[] lines = fh.getLines();
        // TODO optimize the following into the frame handler itself
        String logs =
            Arrays.stream(lines).skip(logReadOffset).map(s -> s.concat("\n")).reduce(String::concat).orElse("-");
        logReadOffset = lines.length;
        return logs;
    }

    @SuppressWarnings("squid:S3655") // sonarlint: Optional value should only be accessed after calling isPresent()
    // this is fixed as if there is no value we either throw an exception or store a
    // new value
    public long getExitCode() {
        if (!exitCode.isPresent()) {
            // check if still running
            ContainerState state = DOCKER.inspectContainerCmd(getId()).exec().getState();
            if (Boolean.TRUE.equals(state.getRunning())) {
                throw new IllegalStateException("Container is still running");
            } else {
                storeExitCode();
                autoRemove();
            }
        }
        return exitCode.get();
    }

    public String getContainerName() {
        return containerName;
    }
}
