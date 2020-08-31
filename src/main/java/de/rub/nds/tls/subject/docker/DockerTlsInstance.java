package de.rub.nds.tls.subject.docker;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.LogsParam;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ContainerState;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.Image;
import com.spotify.docker.client.messages.Volume;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.constants.TlsImageLabels;
import de.rub.nds.tls.subject.exceptions.CertVolumeNotFoundException;
import de.rub.nds.tls.subject.exceptions.TlsVersionNotFoundException;
import de.rub.nds.tls.subject.instance.TlsInstance;
import de.rub.nds.tls.subject.params.ParameterProfile;
import de.rub.nds.tls.subject.properties.ImageProperties;

public abstract class DockerTlsInstance implements TlsInstance {
    protected static final DockerClient DOCKER = DockerClientManager.getDockerClient();
    private static final Logger LOGGER = LogManager.getLogger();

    private String containerId;
    protected final Image image;
    private ContainerConfig containerConfig;
    private Optional<Long> exitCode = Optional.empty();
    private boolean autoRemove;
    private int logReadOffset = 0;
    protected final ParameterProfile parameterProfile;
    protected final ImageProperties imageProperties;
    protected List<DockerExecInstance> childExecs = new LinkedList<>();

    public DockerTlsInstance(ParameterProfile profile, ImageProperties imageProperties, String version, ConnectionRole role, boolean autoRemove) throws DockerException, InterruptedException {
        if (profile == null) {
            throw new NullPointerException("profile may not be null");
        }
        if (imageProperties == null) {
            throw new NullPointerException("imageProperties may not be null");
        }
        this.autoRemove = autoRemove;
        this.parameterProfile = profile;
        this.imageProperties = imageProperties;
        this.image = DOCKER.listImages(
                DockerClient.ListImagesParam.withLabel(TlsImageLabels.IMPLEMENTATION.getLabelName(), profile.getType().name().toLowerCase()),
                DockerClient.ListImagesParam.withLabel(TlsImageLabels.VERSION.getLabelName(), version),
                DockerClient.ListImagesParam.withLabel(TlsImageLabels.CONNECTION_ROLE.getLabelName(),
                        role.toString().toLowerCase()))
                .stream().findFirst()
                .orElseThrow(TlsVersionNotFoundException::new);
    }

    protected HostConfig.Builder createHostConfig(HostConfig.Builder builder) throws DockerException, InterruptedException {
        Volume volume = DOCKER.listVolumes(DockerClient.ListVolumesParam.name("cert-data")).volumes().stream()
                .findFirst()
                .orElseThrow(CertVolumeNotFoundException::new);

        return builder
                .appendBinds(HostConfig.Bind.from(volume)
                        .to("/cert/")
                        .readOnly(true)
                        .noCopy(true)
                        .build());
    }

    protected ContainerConfig.Builder createContainerConfig(ContainerConfig.Builder builder) throws DockerException, InterruptedException {
        return builder
                .image(image.id())
                .attachStderr(true)
                .attachStdout(true)
                .attachStdin(true)
                .tty(true)
                .stdinOnce(true)
                .openStdin(true)
                .hostConfig(createHostConfig(HostConfig.builder()).build());
        // missing: hostConfig, exposedPorts, cmd
    }

    public void ensureContainerConfigExists() throws CertVolumeNotFoundException, DockerException, InterruptedException {
        if (containerConfig == null) {
            containerConfig = createContainerConfig(ContainerConfig.builder()).build();
        }
    }

    protected String createContainer() throws DockerException, InterruptedException {
        if (this.image == null) {
            throw new IllegalStateException("Container could not be created, image is missing");
        }
        ensureContainerConfigExists();
        ContainerCreation container = DOCKER.createContainer(containerConfig);
        List<String> warnings = container.warnings();
        if (warnings != null && !warnings.isEmpty() && LOGGER.isWarnEnabled()) {
            LOGGER.warn("During container creation the following warnings were raised:");
            for (String warning : warnings) {
                LOGGER.warn(warning);
            }
        }
        return container.id();
    }

    public void ensureContainerExists() throws DockerException, InterruptedException {
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

    @Override
    public void start() throws DockerException, InterruptedException {
        ensureContainerExists();
        DOCKER.startContainer(getId());
        // TODO replicate updateInstancePort stuff
    }

    @Override
    public void remove() throws DockerException, InterruptedException {
        DOCKER.removeContainer(getId());
        closeChildren();
        containerId = null;
    }

    private void autoRemove() throws DockerException, InterruptedException {
        if (autoRemove) {
            remove();
        }
    }

    private void storeExitCode() throws DockerException, InterruptedException {
        this.exitCode = Optional.of(DOCKER.inspectContainer(getId()).state().exitCode());
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

    @Override
    public void stop(int secondsToWaitBeforeKilling) throws DockerException, InterruptedException {
        DOCKER.stopContainer(getId(), secondsToWaitBeforeKilling);
        closeChildren();
        storeExitCode();
        autoRemove();
    }

    @Override
    public void stop() throws DockerException, InterruptedException {
        stop(2);
    }

    @Override
    public void kill() throws DockerException, InterruptedException {
        DOCKER.killContainer(getId());
        closeChildren();
        storeExitCode();
        autoRemove();
    }

    @Override
    @SuppressWarnings("squid:S2142") // sonarlint: "InterruptedException" should not be ignored
    // we rethrow the interrupted exception a bit later
    public void close() {
        boolean interrupted = false;
        closeChildren();
        if (autoRemove) {
            try {
                DOCKER.killContainer(getId());
            } catch (DockerException e) {
                LOGGER.warn("Failed to kill container on close()");
            } catch (InterruptedException e) {
                interrupted = true;
            }
            try {
                remove();
            } catch (DockerException e) {
                // we did our best
                LOGGER.warn("Failed to remove container on close()", e);
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void restart() throws DockerException, InterruptedException {
        DOCKER.restartContainer(getId());
    }

    @Override
    public String getId() {
        return containerId;
    }

    @Override
    public String getLogs() throws DockerException, InterruptedException {
        String logs;
        LogStream logStream = DOCKER.logs(getId(), LogsParam.stderr(), LogsParam.stdout());
        String[] lines = logStream.readFully().split("\r\n|\r|\n");
        logs = Arrays.stream(lines)
                .skip(logReadOffset)
                .map(s -> s.concat("\n"))
                .reduce(String::concat)
                .orElse("-");
        logReadOffset = lines.length;
        return logs;
    }

    @Override
    @SuppressWarnings("squid:S3655") // sonarlint: Optional value should only be accessed after calling isPresent()
    // this is fixed as if there is no value we either throw an exception or store a
    // new value
    public long getExitCode() throws DockerException, InterruptedException {
        if (!exitCode.isPresent()) {
            // check if still running
            ContainerState state = DOCKER.inspectContainer(getId()).state();
            if (state.running()) {
                throw new IllegalStateException("Container is still running");
            } else {
                storeExitCode();
                autoRemove();
            }
        }
        return exitCode.get();
    }
}