package de.rub.nds.tls.subject.docker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.LogsParam;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.ContainerNotFoundException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.exceptions.ImageNotFoundException;
import com.spotify.docker.client.exceptions.VolumeNotFoundException;
import com.spotify.docker.client.messages.*;
import de.rub.nds.tls.subject.TlsServer;
import de.rub.nds.tls.subject.TlsServerManager;
import de.rub.nds.tls.subject.exceptions.CertVolumeNotFoundException;
import de.rub.nds.tls.subject.exceptions.TlsVersionNotFoundException;
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
    private static final String VERSION_LABEL = "server_version";
    private final Map<String, Integer> logReadOffset;
    private String name;
    private String version;
    private String[] startParmeters;
    private int internalPort;
    
    DockerSpotifyTlsServerManager() {
        logReadOffset = new HashMap<>();
    }

    DockerSpotifyTlsServerManager setTlsServerNameVersion(String name, String version) {
        this.name = name;
        this.version = version;
        return this;
    }

    DockerSpotifyTlsServerManager setStartParameter(String... startParmeters) {
        this.startParmeters = startParmeters;
        return this;
    }

    DockerSpotifyTlsServerManager setInternalPort(int internalPort) {
        this.internalPort = internalPort;
        return this;
    }

    @Override
    public TlsServer getTlsServer() {
        int port_container_external;
        try {
            Image image = docker.listImages(DockerClient.ListImagesParam.withLabel(SERVER_LABEL, name),DockerClient.ListImagesParam.withLabel(VERSION_LABEL, version)).stream()
                    .findFirst()
                    .orElseThrow(() -> new TlsVersionNotFoundException());
            Volume volume = docker.listVolumes(DockerClient.ListVolumesParam.name("cert-data")).volumes().stream()
                    .findFirst()
                    .orElseThrow(() -> new CertVolumeNotFoundException());
            String id = docker.createContainer(
                    ContainerConfig.builder()
                            .image(image.id())
                            .hostConfig(HostConfig.builder()
                                    .portBindings(Collections.singletonMap(internalPort + "/tcp", Collections.singletonList(PortBinding.randomPort("127.0.0.42"))))
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
                            .exposedPorts(internalPort + "/tcp")
                            .attachStderr(true)
                            .attachStdout(true)
                            .attachStdin(true)
                            .tty(true)
                            .stdinOnce(true)
                            .openStdin(true)
                            //.entrypoint("strace")
                            .cmd(startParmeters)
                            .build(),
                    name + "_" + RandomStringUtils.randomAlphanumeric(8)
            ).id();
            LOGGER.debug("Starting TLS Server " + id);
            docker.startContainer(id);
            
            port_container_external = new Integer(docker.inspectContainer(id).networkSettings().ports().get(internalPort + "/tcp").get(0).hostPort());
            TlsServer tlsServer = new TlsServer(id, port_container_external, name, this);
            //LOGGER.trace(getLogsFromTlsServer(tlsServer)); //skip server startup output
            LOGGER.debug(String.format("Started TLS Server %s : %s(%s)", id, name, version));

            return tlsServer;
        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getServerName(TlsServer tlsServer) {
        return name;
    }

    @Override
    public void killTlsServer(TlsServer tlsServer) {
        LOGGER.debug("Shutting down TLS Server " + tlsServer.id);
        try {
            docker.stopContainer(tlsServer.id, 2);
            tlsServer.exitCode = docker.inspectContainer(tlsServer.id).state().exitCode();
            /*tlsServer.exitCode = eventMap.getOrDefault(tlsServer.id, -1);
            eventMap.remove(tlsServer.id);*/
            docker.removeContainer(tlsServer.id);
        } catch (ContainerNotFoundException ignored) {
        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getLogsFromTlsServer(TlsServer tlsServer) {
        String logs = "-";
        try {
            LogStream logStream = docker.logs(tlsServer.id, LogsParam.stderr(), LogsParam.stdout());
            String[] lines = logStream.readFully().split("\r\n|\r|\n");
            logs = Arrays.stream(lines)
                    .skip(logReadOffset.getOrDefault(tlsServer.id, 0))
                    .map(s -> s.concat("\n"))
                    .reduce(String::concat)
                    .orElse("-");
            logReadOffset.put(tlsServer.id, lines.length);
        } catch (ContainerNotFoundException e) {
            return logs;
        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
        }
        return logs;
    }
}
