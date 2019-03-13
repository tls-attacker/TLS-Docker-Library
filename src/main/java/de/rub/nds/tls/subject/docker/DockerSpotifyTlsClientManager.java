package de.rub.nds.tls.subject.docker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.LogsParam;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.ContainerNotFoundException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.Image;
import de.rub.nds.tls.subject.TlsClient;
import de.rub.nds.tls.subject.TlsClientManager;
import de.rub.nds.tls.subject.exceptions.TlsVersionNotFoundException;
import de.rub.nds.tls.subject.params.Parameter;
import de.rub.nds.tls.subject.params.ParameterProfile;
import de.rub.nds.tls.subject.properties.ClientImageProperties;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Mange a TLS-Client via Docker
 *
 * One instance is needed for each client type.
 */
public class DockerSpotifyTlsClientManager implements TlsClientManager {

    private static final Logger LOGGER = LogManager.getLogger(DockerSpotifyTlsClientManager.class);
    private static final DockerClient docker = new DefaultDockerClient("unix:///var/run/docker.sock");
    private static final String CLIENT_LABEL = "client_type";
    private static final String CLIENT_VERSION_LABEL = "client_version";
    private final Map<String, Integer> logReadOffset;

    DockerSpotifyTlsClientManager() {
        logReadOffset = new HashMap<>();
    }

    @Override
    public TlsClient getTlsClient(ClientImageProperties properties, ParameterProfile profile, String version, String host, int port) {
        try {
            Image image = docker.listImages(DockerClient.ListImagesParam.withLabel(CLIENT_LABEL, profile.getType().name().toLowerCase()), DockerClient.ListImagesParam.withLabel(CLIENT_VERSION_LABEL, version)).stream()
                    .findFirst()
                    .orElseThrow(() -> new TlsVersionNotFoundException());
            String id = docker.createContainer(
                    ContainerConfig.builder()
                            .image(image.id())
                            .attachStderr(true)
                            .attachStdout(true)
                            .attachStdin(true)
                            .tty(true)
                            .stdinOnce(true)
                            .openStdin(true)
                            //.entrypoint("strace")
                            .cmd(convertProfileToParams(profile, host, port))
                            .build(),
                    profile.getType().name() + "_" + RandomStringUtils.randomAlphanumeric(8)
            ).id();
            LOGGER.debug("Starting TLS Client " + id);
            docker.startContainer(id);

            TlsClient tlsClient = new TlsClient(id, host, port, profile.getType().name(), this);
            //LOGGER.trace(getLogsFromTlsClient(tlsClient)); //skip client startup output
            LOGGER.debug(String.format("Started TLS Client %s : %s(%s)", id, profile.getType().name(), version));

            return tlsClient;
        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;

    }

    private String[] convertProfileToParams(ParameterProfile profile, String host, int port) {
        StringBuilder finalParams = new StringBuilder();
        for (Parameter param : profile.getParameterList()) {
            finalParams.append(param.getCmdParameter());
            finalParams.append(" ");
        }
        return finalParams.toString().replace("[host]", host).replace("[port]", "" + port).split(" ");
    }

    @Override
    public TlsClient getTlsClient(ClientImageProperties properties, ParameterProfile profile, String host, int port) {
        return this.getTlsClient(properties, profile, properties.getDefaultVersion(), host, port);
    }

    @Override
    public void killTlsClient(TlsClient tlsClient) {
        LOGGER.debug("Shutting down TLS Client " + tlsClient.getId());
        try {
            docker.stopContainer(tlsClient.getId(), 2);
            tlsClient.setExitCode(docker.inspectContainer(tlsClient.getId()).state().exitCode());
            /*tlsClient.exitCode = eventMap.getOrDefault(tlsClient.id, -1);
            eventMap.remove(tlsClient.id);*/
            docker.removeContainer(tlsClient.getId());
        } catch (ContainerNotFoundException e) {
            LOGGER.debug(e);
        } catch (DockerException | InterruptedException e) {
            LOGGER.debug(e);
        }
    }

    @Override
    public String getLogsFromTlsClient(TlsClient tlsClient) {
        String logs = "-";
        try {
            LogStream logStream = docker.logs(tlsClient.getId(), LogsParam.stderr(), LogsParam.stdout());
            String[] lines = logStream.readFully().split("\r\n|\r|\n");
            logs = Arrays.stream(lines)
                    .skip(logReadOffset.getOrDefault(tlsClient.getId(), 0))
                    .map(s -> s.concat("\n"))
                    .reduce(String::concat)
                    .orElse("-");
            logReadOffset.put(tlsClient.getId(), lines.length);
        } catch (ContainerNotFoundException e) {
            return logs;
        } catch (DockerException | InterruptedException e) {
            LOGGER.debug(e);
        }
        return logs;
    }
}
