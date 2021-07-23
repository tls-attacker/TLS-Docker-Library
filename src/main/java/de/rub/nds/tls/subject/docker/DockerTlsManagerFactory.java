package de.rub.nds.tls.subject.docker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Image;

import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.HostInfo;
import de.rub.nds.tls.subject.TlsImplementationType;
import de.rub.nds.tls.subject.constants.TlsImageLabels;
import de.rub.nds.tls.subject.constants.TransportType;
import de.rub.nds.tls.subject.exceptions.DefaultProfileNotFoundException;
import de.rub.nds.tls.subject.exceptions.PropertyNotFoundException;
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

    private static final com.github.dockerjava.api.DockerClient DOCKER = DockerClientManager.getDockerClient();

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
        protected UnaryOperator<HostConfig> hostConfigHook;
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

        public T hostConfigHook(UnaryOperator<HostConfig> value) {
            hostConfigHook = value;
            return (T) this;
        }

        public abstract DockerTlsInstance build() throws DockerException, InterruptedException;
    }

    public static class TlsClientInstanceBuilder extends TlsInstanceBuilder<TlsClientInstanceBuilder> {

        protected boolean connectOnStartup = true;

        public TlsClientInstanceBuilder(TlsImplementationType type, String version, TransportType transportType) {
            super(type, version, ConnectionRole.CLIENT, transportType);
        }

        @Override
        public DockerTlsClientInstance build() throws DockerException, InterruptedException {
            return new DockerTlsClientInstance(profile, imageProperties, version, autoRemove, new HostInfo(ip, hostname, port, transportType), additionalParameters, parallelize, insecureConnection,
                    connectOnStartup, hostConfigHook);
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
        public DockerTlsServerInstance build() throws DockerException, InterruptedException {
            return new DockerTlsServerInstance(profile, imageProperties, version, autoRemove, new HostInfo(ip, hostname, port, transportType), additionalParameters, parallelize, insecureConnection,
                    hostConfigHook);
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
        Map<String, String> labels = new HashMap<>();
        labels.put(TlsImageLabels.IMPLEMENTATION.getLabelName(), type.name().toLowerCase());
        labels.put(TlsImageLabels.CONNECTION_ROLE.getLabelName(), role.toString().toLowerCase());
        List<Image> serverImageList = DOCKER.listImagesCmd().withLabelFilter(labels).withDanglingFilter(false).exec();
        for (Image image : serverImageList) {
            if (image.getLabels() != null) {
                String version = image.getLabels().get(TlsImageLabels.VERSION.getLabelName());
                if (version != null) {
                    versionList.add(version);
                }
            }
        }
        return versionList;
    }

    public static List<Image> getAllImages() {
        return DOCKER.listImagesCmd().withLabelFilter(TlsImageLabels.IMPLEMENTATION.getLabelName()).withDanglingFilter(false).exec();
    }
}
