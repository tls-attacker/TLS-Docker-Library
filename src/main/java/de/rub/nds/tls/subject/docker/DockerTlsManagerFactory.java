package de.rub.nds.tls.subject.docker;

import java.util.LinkedList;
import java.util.List;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Image;

import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.HostInfo;
import de.rub.nds.tls.subject.TlsImplementationType;
import de.rub.nds.tls.subject.constants.TlsImageLabels;
import de.rub.nds.tls.subject.constants.TransportType;
import de.rub.nds.tls.subject.exceptions.DefaultProfileNotFoundException;
import de.rub.nds.tls.subject.exceptions.PropertyNotFoundException;
import de.rub.nds.tls.subject.instance.TlsClientInstance;
import de.rub.nds.tls.subject.instance.TlsInstance;
import de.rub.nds.tls.subject.instance.TlsServerInstance;
import de.rub.nds.tls.subject.params.ParameterProfile;
import de.rub.nds.tls.subject.params.ParameterProfileManager;
import de.rub.nds.tls.subject.properties.ImageProperties;
import de.rub.nds.tls.subject.properties.PropertyManager;

/**
 * Creates TLS-Server or TLS-Client Instances as Docker Container Holds the
 * Config for each TLS-Server or TLS-Client
 */
public class DockerTlsManagerFactory {
    private DockerTlsManagerFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final DockerClient DOCKER = DockerClientManager.getDockerClient();

    private static final int DEFAULT_PORT = 4433;

    @SuppressWarnings("unchecked")
    public abstract static class TlsInstanceBuilder<T extends TlsInstanceBuilder<T>> {
        protected final ParameterProfile profile;
        protected final ImageProperties imageProperties;
        protected final String version;
        protected boolean autoRemove = true;
        // shared constructor parameters
        // Host Info:
        protected final TransportType transportType;
        protected String ip = null;
        protected String hostname = null;
        protected int port = DEFAULT_PORT;
        // remaining shared params
        protected String additionalParameters = null;
        protected boolean parallelize = false;
        protected boolean insecureConnection = false;

        public TlsInstanceBuilder(TlsImplementationType type, String version, ConnectionRole role, TransportType transportType) {
            this.profile = retrieveParameterProfile(type, version, role);
            this.imageProperties = retrieveImageProperties(role, type);
            this.version = version;
            this.transportType = transportType;
        }

        public T autoRemove(boolean value) {
            autoRemove = value;
            return (T) this;
        }

        public T ip(String value) {
            ip = value;
            return (T) this;
        }

        public T hostname(String value) {
            hostname = value;
            return (T) this;
        }

        public T port(int value) {
            port = value;
            return (T) this;
        }

        public T additionalParameters(String value) {
            additionalParameters = value;
            return (T) this;
        }

        public T parallelize(boolean value) {
            parallelize = value;
            return (T) this;
        }

        public T insecureConnection(boolean value) {
            insecureConnection = value;
            return (T) this;
        }

        public abstract TlsInstance build() throws DockerException, InterruptedException;
    }

    public static class TlsClientInstanceBuilder extends TlsInstanceBuilder<TlsClientInstanceBuilder> {

        protected boolean connectOnStartup = true;

        public TlsClientInstanceBuilder(TlsImplementationType type, String version, TransportType transportType) {
            super(type, version, ConnectionRole.CLIENT, transportType);
        }

        @Override
        public TlsClientInstance build() throws DockerException, InterruptedException {
            return new DockerTlsClientInstance(profile, imageProperties, version, autoRemove, new HostInfo(ip, hostname, port, transportType), additionalParameters, parallelize, insecureConnection,
                    connectOnStartup);
        }

        public TlsClientInstanceBuilder connectOnStartup(boolean value) {
            connectOnStartup = value;
            return this;
        }

    }

    public static class TlsServerInstanceBuilder extends TlsInstanceBuilder<TlsServerInstanceBuilder> {

        public TlsServerInstanceBuilder(TlsImplementationType type, String version, TransportType transportType) {
            super(type, version, ConnectionRole.SERVER, transportType);
        }

        @Override
        public TlsServerInstance build() throws DockerException, InterruptedException {
            return new DockerTlsServerInstance(profile, imageProperties, version, autoRemove, new HostInfo(ip, hostname, port, transportType), additionalParameters, parallelize, insecureConnection);
        }

    }

    public static TlsClientInstanceBuilder getTlsClientBuilder(TlsImplementationType type, String version) {
        return new TlsClientInstanceBuilder(type, version, TransportType.TCP);
    }

    public static TlsClientInstanceBuilder getDTlsClientBuilder(TlsImplementationType type, String version) {
        return new TlsClientInstanceBuilder(type, version, TransportType.UDP);
    }

    public static TlsServerInstanceBuilder getTlsServerBuilder(TlsImplementationType type, String version) {
        return new TlsServerInstanceBuilder(type, version, TransportType.TCP);
    }

    public static TlsServerInstanceBuilder getDTlsServerBuilder(TlsImplementationType type, String version) {
        return new TlsServerInstanceBuilder(type, version, TransportType.UDP);
    }

    public static boolean clientExists(TlsImplementationType type, String version) {
        return checkExists(type, version, ConnectionRole.CLIENT);
    }

    public static boolean serverExists(TlsImplementationType type, String version) {
        return checkExists(type, version, ConnectionRole.SERVER);
    }

    private static boolean checkExists(TlsImplementationType type, String version, ConnectionRole role) {
        return PropertyManager.instance().getProperties(role, type) != null && ParameterProfileManager.instance().getProfile(type, version, role) != null;
    }

    public static ImageProperties retrieveImageProperties(ConnectionRole role, TlsImplementationType type) throws PropertyNotFoundException {
        ImageProperties properties = PropertyManager.instance().getProperties(role, type);
        if (properties == null) {
            throw new PropertyNotFoundException("Could not find a Property for " + role.name() + ": " + type.name());
        }
        return properties;
    }

    public static ParameterProfile retrieveParameterProfile(TlsImplementationType type, String version, ConnectionRole role) throws DefaultProfileNotFoundException {
        ParameterProfile profile = ParameterProfileManager.instance().getProfile(type, version, role);
        if (profile == null) {
            throw new DefaultProfileNotFoundException("Could not find a Profile for " + role.name() + ": " + type.name() + ":" + version);
        }
        return profile;
    }

    public static List<String> getAvailableVersions(ConnectionRole role, TlsImplementationType type) {
        List<String> versionList = new LinkedList<>();
        try {
            List<Image> serverImageList = DOCKER.listImages(
                    DockerClient.ListImagesParam.withLabel(TlsImageLabels.IMPLEMENTATION.getLabelName(), type.name().toLowerCase()),
                    DockerClient.ListImagesParam.withLabel(TlsImageLabels.CONNECTION_ROLE.getLabelName(), role.toString().toLowerCase()));
            for (Image image : serverImageList) {
                if (image.labels() != null) {
                    String version = image.labels().get(TlsImageLabels.VERSION.getLabelName());
                    if (version != null) {
                        versionList.add(version);
                    }
                }
            }
            return versionList;
        } catch (DockerException | InterruptedException ex) {
            throw new RuntimeException("Could not retrieve available " + role.name() + " Versions!", ex);
        }
    }

    public static List<Image> getAllImages() {
        try {
            return DOCKER.listImages(
                    DockerClient.ListImagesParam.withLabel(TlsImageLabels.IMPLEMENTATION.getLabelName()),
                    DockerClient.ListImagesParam.danglingImages(false));
        } catch (Exception e) {
            throw new RuntimeException("Could not receive images", e);
        }

    }
}
