package de.rub.nds.tls.subject.docker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.LogsParam;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.ContainerNotFoundException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.*;
import de.rub.nds.tls.subject.TlsServer;
import de.rub.nds.tls.subject.TlsServerManager;
import de.rub.nds.tls.subject.exceptions.CertVolumeNotFoundException;
import de.rub.nds.tls.subject.exceptions.TlsVersionNotFoundException;
import de.rub.nds.tls.subject.params.Parameter;
import de.rub.nds.tls.subject.params.ParameterProfile;
import de.rub.nds.tls.subject.properties.ServerImageProperties;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Mange a TLS-Server via Docker
 *
 * One instance is needed for each server type.
 */
public class DockerSpotifyTlsServerManager implements TlsServerManager {

    private static final Logger LOGGER = LogManager.getLogger(DockerSpotifyTlsServerManager.class);
    private static final DockerClient docker = new DefaultDockerClient("unix:///var/run/docker.sock");
    private static final String SERVER_LABEL = "server_type";
    private static final String SERVER_VERSION_LABEL = "server_version";
    private final Map<String, Integer> logReadOffset;

    DockerSpotifyTlsServerManager() {
        logReadOffset = new HashMap<>();
    }

    @Override
    public TlsServer getTlsServer(ServerImageProperties properties, ParameterProfile profile, String version, String additionalParameters) {
        int port_container_external;
        try {
            Image image = docker.listImages(DockerClient.ListImagesParam.withLabel(SERVER_LABEL, profile.getType().name().toLowerCase()), DockerClient.ListImagesParam.withLabel(SERVER_VERSION_LABEL, version)).stream()
                    .findFirst()
                    .orElseThrow(() -> new TlsVersionNotFoundException());
            Volume volume = docker.listVolumes(DockerClient.ListVolumesParam.name("cert-data")).volumes().stream()
                    .findFirst()
                    .orElseThrow(() -> new CertVolumeNotFoundException());
            String id = docker.createContainer(
                    ContainerConfig.builder()
                            .image(image.id())
                            .hostConfig(HostConfig.builder()
                                    .portBindings(Collections.singletonMap(properties.getInternalPort() + "/tcp", Collections.singletonList(PortBinding.randomPort("127.0.0.42"))))
                                    .binds(HostConfig.Bind.builder()
                                            .from(volume)
                                            .readOnly(true)
                                            .noCopy(true)
                                            .to("/cert/")
                                            .build())
                                    //.autoRemove(true)
                                    .readonlyRootfs(true)
                                    //.capAdd("SYS_PTRACE")
                                    .build())
                            .exposedPorts(properties.getInternalPort() + "/tcp")
                            .attachStderr(true)
                            .attachStdout(true)
                            .attachStdin(true)
                            .tty(true)
                            .stdinOnce(true)
                            .openStdin(true)
                            //.entrypoint("strace")
                            .cmd(convertProfileToParams(profile, properties.getInternalPort(), properties.getDefaultKeyPath(), properties.getDefaultCertPath(), additionalParameters))
                            .build(),
                    profile.getType().name() + "_" + RandomStringUtils.randomAlphanumeric(8)
            ).id();
            LOGGER.debug("Starting TLS Server " + id);
            docker.startContainer(id);

            port_container_external = new Integer(docker.inspectContainer(id).networkSettings().ports().get(properties.getInternalPort() + "/tcp").get(0).hostPort());
            TlsServer tlsServer = new TlsServer(id, port_container_external, profile.getType().name(), this);
            //LOGGER.trace(getLogsFromTlsServer(tlsServer)); //skip server startup output
            LOGGER.debug(String.format("Started TLS Server %s : %s(%s)", id, profile.getType().name(), version));
            LOGGER.debug(getLogsFromTlsServer(tlsServer));
            return tlsServer;
        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;

    }

    private String[] convertProfileToParams(ParameterProfile profile, int port, String certPath, String keyPath, String additionalParameters) {
        StringBuilder finalParams = new StringBuilder();
        for (Parameter param : profile.getParameterList()) {
            finalParams.append(param.getCmdParameter());
            finalParams.append(" ");
        }
        if (additionalParameters != null) {
            finalParams.append(additionalParameters);
        }
        String afterReplace = finalParams.toString().replace("[cert]", certPath).replace("[key]", keyPath).replace("[port]", "" + port);
        LOGGER.debug("Final parameters:" + afterReplace);
        return afterReplace.trim().split(" ");
    }

    @Override
    public TlsServer getTlsServer(ServerImageProperties properties, ParameterProfile profile) {
        return this.getTlsServer(properties, profile, properties.getDefaultVersion(), null);
    }

    @Override
    public void killTlsServer(TlsServer tlsServer) {
        LOGGER.debug("Shutting down TLS Server " + tlsServer.getId());
        try {
            docker.stopContainer(tlsServer.getId(), 2);
            tlsServer.setExitCode(docker.inspectContainer(tlsServer.getId()).state().exitCode());
            /*tlsServer.exitCode = eventMap.getOrDefault(tlsServer.id, -1);
            eventMap.remove(tlsServer.id);*/
            docker.removeContainer(tlsServer.getId());
        } catch (ContainerNotFoundException e) {
            LOGGER.debug(e);
        } catch (DockerException | InterruptedException e) {
            LOGGER.debug(e);
        }
    }

    @Override
    public String getLogsFromTlsServer(TlsServer tlsServer) {
        String logs = "-";
        try {
            LogStream logStream = docker.logs(tlsServer.getId(), LogsParam.stderr(), LogsParam.stdout());
            String[] lines = logStream.readFully().split("\r\n|\r|\n");
            logs = Arrays.stream(lines)
                    .skip(logReadOffset.getOrDefault(tlsServer.getId(), 0))
                    .map(s -> s.concat("\n"))
                    .reduce(String::concat)
                    .orElse("-");
            logReadOffset.put(tlsServer.getId(), lines.length);
        } catch (ContainerNotFoundException e) {
            return logs;
        } catch (DockerException | InterruptedException e) {
            LOGGER.debug(e);
        }
        return logs;
    }
}
