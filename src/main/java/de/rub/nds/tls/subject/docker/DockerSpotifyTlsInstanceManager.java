package de.rub.nds.tls.subject.docker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.LogsParam;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.ContainerNotFoundException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.Image;
import com.spotify.docker.client.messages.PortBinding;
import com.spotify.docker.client.messages.Volume;
import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.TlsInstance;
import de.rub.nds.tls.subject.TlsInstanceManager;
import de.rub.nds.tls.subject.exceptions.CertVolumeNotFoundException;
import de.rub.nds.tls.subject.exceptions.TlsVersionNotFoundException;
import de.rub.nds.tls.subject.params.Parameter;
import de.rub.nds.tls.subject.params.ParameterProfile;
import de.rub.nds.tls.subject.properties.ImageProperties;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manage a TLS-Instance via Docker
 *
 * One instance is needed for each client or server type.
 */
public class DockerSpotifyTlsInstanceManager implements TlsInstanceManager {
    private static final Logger LOGGER = LogManager.getLogger(DockerSpotifyTlsInstanceManager.class);
    private static final DockerClient docker = new DefaultDockerClient("unix:///var/run/docker.sock");
    private static final String CLIENT_LABEL = "client_type";
    private static final String CLIENT_VERSION_LABEL = "client_version";
    private static final String SERVER_LABEL = "server_type";
    private static final String SERVER_VERSION_LABEL = "server_version";
    private final Map<String, Integer> logReadOffset;
    
    DockerSpotifyTlsInstanceManager() {
        logReadOffset = new HashMap<>();
    }
    
    @Override
    public TlsInstance getTlsClient(ImageProperties properties, ParameterProfile profile, String host, int port) {
        return this.getTlsClient(properties, profile, properties.getDefaultVersion(), host, port);
    }
    
    @Override
    public TlsInstance getTlsClient(ImageProperties properties, ParameterProfile profile, String version, String host, int port) {
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
                            .cmd(convertClientProfileToParams(profile, host, port))
                            .build(),
                    profile.getType().name() + "_" + RandomStringUtils.randomAlphanumeric(8)
            ).id();
            LOGGER.debug("Starting TLS Client " + id);
            docker.startContainer(id);

            TlsInstance tlsClient = new TlsInstance(id, ConnectionRole.CLIENT, host, port, profile.getType().name(), this);
            //LOGGER.trace(getLogsFromTlsClient(tlsClient)); //skip client startup output
            LOGGER.debug(String.format("Started TLS Client %s : %s(%s)", id, profile.getType().name(), version));

            return tlsClient;
        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
        
    }
    
    @Override
    public TlsInstance getTlsServer(ImageProperties properties, ParameterProfile profile, String host) {
        return this.getTlsServer(properties, profile, properties.getDefaultVersion(), host);
    }
    
    @Override
    public TlsInstance getTlsServer(ImageProperties properties, ParameterProfile profile, String version, String host) {
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
                            .cmd(convertServerProfileToParams(profile, properties.getInternalPort(), properties.getDefaultKeyPath(), properties.getDefaultCertPath()))
                            .build(),
                    profile.getType().name() + "_" + RandomStringUtils.randomAlphanumeric(8)
            ).id();
            LOGGER.debug("Starting TLS Server " + id);
            docker.startContainer(id);

            port_container_external = new Integer(docker.inspectContainer(id).networkSettings().ports().get(properties.getInternalPort() + "/tcp").get(0).hostPort());
            TlsInstance tlsServer = new TlsInstance(id, ConnectionRole.SERVER, host, port_container_external, profile.getType().name(), this);       
            //LOGGER.trace(getLogsFromTlsServer(tlsServer)); //skip server startup output
            LOGGER.debug(String.format("Started TLS Server %s : %s(%s)", id, profile.getType().name(), version));

            return tlsServer;
        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;

    }
    
    private String[] convertClientProfileToParams(ParameterProfile profile, String host, int port) {
        StringBuilder finalParams = new StringBuilder();
        for (Parameter param : profile.getParameterList()) {
            finalParams.append(param.getCmdParameter());
            finalParams.append(" ");
        }
        return finalParams.toString().replace("[host]", host).replace("[port]", "" + port).split(" ");
    }
    
    private String[] convertServerProfileToParams(ParameterProfile profile, int port, String certPath, String keyPath) {
        StringBuilder finalParams = new StringBuilder();
        for (Parameter param : profile.getParameterList()) {
            finalParams.append(param.getCmdParameter());
            finalParams.append(" ");
        }
        return finalParams.toString().replace("[cert]", certPath).replace("[key]", keyPath).replace("[port]", "" + port).split(" ");
    }
    
    @Override
    public void killTlsInstance(TlsInstance tlsInstance) {
        LOGGER.debug("Shutting down TLS Instance " + tlsInstance.getId());
        try {
            docker.stopContainer(tlsInstance.getId(), 2);
            tlsInstance.setExitCode(docker.inspectContainer(tlsInstance.getId()).state().exitCode());
            docker.removeContainer(tlsInstance.getId());
        } catch (ContainerNotFoundException e) {
            LOGGER.debug(e);
        } catch (DockerException | InterruptedException e) {
            LOGGER.debug(e);
        }
    }

    @Override
    public String getLogsFromTlsInstance(TlsInstance tlsInstance) {
        String logs = "-";
        try {
            LogStream logStream = docker.logs(tlsInstance.getId(), LogsParam.stderr(), LogsParam.stdout());
            String[] lines = logStream.readFully().split("\r\n|\r|\n");
            logs = Arrays.stream(lines)
                    .skip(logReadOffset.getOrDefault(tlsInstance.getId(), 0))
                    .map(s -> s.concat("\n"))
                    .reduce(String::concat)
                    .orElse("-");
            logReadOffset.put(tlsInstance.getId(), lines.length);
        } catch (ContainerNotFoundException e) {
            return logs;
        } catch (DockerException | InterruptedException e) {
            LOGGER.debug(e);
        }
        return logs;
    }
}
