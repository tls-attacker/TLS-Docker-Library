package de.rub.nds.tls.subject.docker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import static com.spotify.docker.client.DockerClient.ListVolumesParam.name;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Image;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javassist.CtClass.version;

/**
 * Creates TLS-Server Instances as Docker Container Holds the Config for each
 * TLS-Server
 */
public class DockerTlsServerManagerFactory {

    private static final DockerClient docker = new DefaultDockerClient("unix:///var/run/docker.sock");

    private DockerTlsServerManagerFactory() {
    }

    private static final String SERVER_LABEL = "server_type";
    private static final String VERSION_LABEL = "server_version";

    public static DockerSpotifyTlsServerManager get(DockerTlsServerType serverType, String version) {
        return new DockerSpotifyTlsServerManager()
                .setTlsServerNameVersion(serverType.getName(), version)
                .setInternalPort(serverType.getInternalPort())
                .setStartParameter(serverType.getParams());
    }

    public static List<String> getAvailableVersions(DockerTlsServerType serverType) {
        List<String> versionList = new LinkedList<>();
        try {
            List<Image> imageList = docker.listImages(DockerClient.ListImagesParam.withLabel(SERVER_LABEL, serverType.getName()));
            for (Image image : imageList) {
                if (image.labels() != null) {
                    String version = image.labels().get(VERSION_LABEL);
                    if (version != null) {
                        versionList.add(version);
                    }
                }
            }
            return versionList;
        } catch (DockerException | InterruptedException ex) {
            throw new RuntimeException("Could not retrieve available Versions!");
        }
    }
}
