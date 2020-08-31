package de.rub.nds.tls.subject.docker;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import com.spotify.docker.client.DockerClient.ExecCreateParam;
import com.spotify.docker.client.DockerClient.ExecStartParameter;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ExecCreation;
import com.spotify.docker.client.messages.ExecState;
import com.spotify.docker.client.messages.HostConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.HostInfo;
import de.rub.nds.tls.subject.instance.TlsClientInstance;
import de.rub.nds.tls.subject.params.ParameterProfile;
import de.rub.nds.tls.subject.properties.ImageProperties;

public class DockerTlsClientInstance extends DockerTlsInstance implements TlsClientInstance {
    private static final String[] EMPTY_STR_ARR = {};
    private static final int EXEC_POLL_INTERVAL_MILLISECONDS = 50;
    private static final Logger LOGGER = LogManager.getLogger();

    private final HostInfo hostInfo;
    private final String additionalParameters;
    private final boolean parallelize;
    private final boolean insecureConnection;
    private final boolean connectOnStartup;

    // TODO move away from HostInfo for client...
    public DockerTlsClientInstance(ParameterProfile profile, ImageProperties imageProperties, String version, boolean autoRemove, HostInfo hostInfo, String additionalParameters, boolean parallelize,
            boolean insecureConnection, boolean connectOnStartup) throws DockerException, InterruptedException {
        super(profile, imageProperties, version, ConnectionRole.CLIENT, autoRemove);
        this.hostInfo = hostInfo;
        this.additionalParameters = additionalParameters;
        this.parallelize = parallelize;
        this.insecureConnection = insecureConnection;
        this.connectOnStartup = connectOnStartup;
    }

    @Override
    protected HostConfig.Builder createHostConfig(HostConfig.Builder builder) throws DockerException, InterruptedException {
        String extraHost = "test:127.0.0.27";
        if (hostInfo.getHostname() != null) {
            extraHost = hostInfo.getHostname() + ":" + hostInfo.getIp();
        }
        return super.createHostConfig(builder)
                .extraHosts(extraHost)
                // TODO: Bind of X11 Settings does not work as expected
                .appendBinds(HostConfig.Bind.from("/tmp/.X11-unix")
                        .to("/tmp/.X11-unix")
                        .build());
    }

    @Override
    protected ContainerConfig.Builder createContainerConfig(ContainerConfig.Builder builder) throws DockerException, InterruptedException {
        String host;
        if (hostInfo.getHostname() == null || imageProperties.isUseIP()) {
            host = hostInfo.getIp();
        } else {
            host = hostInfo.getHostname();
        }

        // no exposed ports
        // TODO we might be interested in exposing the client-entrypoint server

        builder = super.createContainerConfig(builder);
        if (connectOnStartup) {
            builder = builder.cmd(parameterProfile.toParameters(host, hostInfo.getPort(), imageProperties, additionalParameters, parallelize, insecureConnection));
        } else {
            builder = builder.entrypoint("client-entrypoint");
        }
        return builder;
    }

    @Override
    public DockerExecInstance connect() throws DockerException, InterruptedException {
        String host;
        if (hostInfo.getHostname() == null || imageProperties.isUseIP()) {
            host = hostInfo.getIp();
        } else {
            host = hostInfo.getHostname();
        }
        return connect(host, hostInfo.getPort());
    }

    @Override
    public DockerExecInstance connect(String host, int targetPort) throws DockerException, InterruptedException {
        return connect(host, targetPort, additionalParameters, parallelize, insecureConnection);
    }

    @Override
    public DockerExecInstance connect(String host, int targetPort, String additionalParameters, Boolean parallelize, Boolean insecureConnection) throws DockerException, InterruptedException {
        List<String> cmd_lst = new LinkedList<>(DOCKER.inspectImage(image.id()).config().entrypoint());
        if (cmd_lst.get(0).equals("client-entrypoint")) {
            cmd_lst.remove(0);
        } else {
            LOGGER.warn("Image {} did not have client-entrypoint as entrypoint", image.id());
        }
        String[] params = parameterProfile.toParameters(host, targetPort, imageProperties, additionalParameters, parallelize, insecureConnection);
        cmd_lst.addAll(Arrays.asList(params));
        ExecCreation exec = DOCKER.execCreate(getId(), cmd_lst.toArray(EMPTY_STR_ARR),
                ExecCreateParam.detach(true),
                ExecCreateParam.attachStdin(false),
                ExecCreateParam.attachStderr(true),
                ExecCreateParam.attachStdout(true),
                ExecCreateParam.tty(true));
        List<String> warnings = exec.warnings();
        if (warnings != null && !warnings.isEmpty() && LOGGER.isWarnEnabled()) {
            LOGGER.warn("During exec creation the following warnings were raised:");
            for (String warning : warnings) {
                LOGGER.warn(warning);
            }
        }
        DockerExecInstance ret = new DockerExecInstance(exec);
        childExecs.add(ret);
        return ret;
    }

}