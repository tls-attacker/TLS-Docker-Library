package de.rub.nds.tls.subject.docker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Image;
import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.TlsImplementationType;
import de.rub.nds.tls.subject.TlsServer;
import de.rub.nds.tls.subject.exceptions.DefaultProfileNotFoundException;
import de.rub.nds.tls.subject.exceptions.PropertyNotFoundException;
import de.rub.nds.tls.subject.params.Parameter;
import de.rub.nds.tls.subject.params.ParameterProfile;
import de.rub.nds.tls.subject.params.ParameterProfileManager;
import de.rub.nds.tls.subject.params.ParameterType;
import de.rub.nds.tls.subject.properties.ImageProperties;
import de.rub.nds.tls.subject.properties.PropertyManager;
import java.util.LinkedList;
import java.util.List;

/**
 * Creates TLS-Server Instances as Docker Container Holds the Config for each
 * TLS-Server
 */
public class DockerTlsServerManagerFactory {

    private static final DockerClient docker = new DefaultDockerClient("unix:///var/run/docker.sock");

    private final ParameterProfileManager parameterManager;
    private final PropertyManager propertyManager;

    public DockerTlsServerManagerFactory() {
        parameterManager = new ParameterProfileManager();
        propertyManager = new PropertyManager();
    }

    private static final String SERVER_LABEL = "server_type";
    private static final String VERSION_LABEL = "server_version";

    public TlsServer get(TlsImplementationType type, String version) {
        return get(type, version, null);
    }

    public TlsServer get(TlsImplementationType type, String version, String additionalParams) {
        ParameterProfile defaultProfile = parameterManager.getDefaultProfile(type, ConnectionRole.SERVER);
        if (defaultProfile == null) {
            throw new DefaultProfileNotFoundException("Could not find a default Profile for: " + type.name() + ":" + version);
        }
        if (additionalParams != null) {
            defaultProfile.getParameterList().add(new Parameter(additionalParams, ParameterType.NONE));
        }
        ImageProperties defaultProperties = propertyManager.getProperties(type);
        if (defaultProperties == null) {
            throw new PropertyNotFoundException("Could not find a default Property for: " + type.name() + ":" + version);

        }
        return new DockerSpotifyTlsServerManager().getTlsServer(defaultProperties, defaultProfile, version);
    }

    public List<String> getAvailableVersions(TlsImplementationType implementationType) {
        List<String> versionList = new LinkedList<>();
        try {
            List<Image> imageList = docker.listImages(DockerClient.ListImagesParam.withLabel(SERVER_LABEL, implementationType.name().toLowerCase()));
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
