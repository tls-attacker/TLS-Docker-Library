/*
 * TLS-Docker-Library - A collection of open source TLS clients and servers
 *
 * Copyright 2017-2022 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tls.subject.docker;

import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Image;
import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.HostInfo;
import de.rub.nds.tls.subject.TlsImplementationType;
import de.rub.nds.tls.subject.constants.TlsImageLabels;
import de.rub.nds.tls.subject.constants.TransportType;
import de.rub.nds.tls.subject.docker.build.DockerBuilder;
import de.rub.nds.tls.subject.exceptions.DefaultProfileNotFoundException;
import de.rub.nds.tls.subject.exceptions.PropertyNotFoundException;
import de.rub.nds.tls.subject.params.ParameterProfile;
import de.rub.nds.tls.subject.params.ParameterProfileManager;
import de.rub.nds.tls.subject.properties.ImageProperties;
import de.rub.nds.tls.subject.properties.PropertyManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Creates TLS-Server or TLS-Client Instances as Docker Container Holds the Config for each
 * TLS-Server or TLS-Client
 */
public class DockerTlsManagerFactory {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final int TIMEOUT = 2;

    private DockerTlsManagerFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final com.github.dockerjava.api.DockerClient DOCKER =
            DockerClientManager.getDockerClient();

    private static final int DEFAULT_PORT = 4433;

    @SuppressWarnings("unchecked")
    public abstract static class TlsInstanceBuilder<T extends TlsInstanceBuilder<T>> {
        protected final ParameterProfile profile;
        protected final ImageProperties imageProperties;
        protected final String version;
        protected Image image;
        protected boolean autoRemove = true;
        // shared constructor parameters
        // Host Info:
        protected final TransportType transportType;
        protected String ip = null;
        protected String hostname = null;
        protected int port = DEFAULT_PORT;
        protected UnaryOperator<HostConfig> hostConfigHook;
        // remaining shared params
        protected String[] cmd = null;
        protected List<ExposedPort> containerExposedPorts = null;
        protected String additionalParameters = null;
        protected String additionalBuildFlags = "";
        protected boolean parallelize = false;
        protected boolean insecureConnection = false;
        protected String containerName;

        private static final String REPOSITORY_LOCATION = "https://hydrogen.cloud.nds.rub.de/nexus";

        private static final String DOCKER_LIBRARY =
                "hydrogen.cloud.nds.rub.de/tls-attacker/docker-library/";

        public TlsInstanceBuilder(
                TlsImplementationType type,
                String version,
                ConnectionRole role,
                TransportType transportType) {
            this.profile = retrieveParameterProfile(type, version, role);
            this.imageProperties = retrieveImageProperties(role, type);
            if (imageProperties.getInternalPort() != null) {
                this.port = imageProperties.getInternalPort();
            }
            this.version = version;
            this.transportType = transportType;
        }

        public TlsInstanceBuilder(Image image, TransportType transportType) {
            this.version = image.getLabels().get(TlsImageLabels.VERSION.getLabelName());
            TlsImplementationType type =
                    TlsImplementationType.fromString(
                            image.getLabels().get(TlsImageLabels.IMPLEMENTATION.getLabelName()));
            ConnectionRole role =
                    ConnectionRole.valueOf(
                            image.getLabels()
                                    .get(TlsImageLabels.CONNECTION_ROLE.getLabelName())
                                    .toUpperCase());
            this.profile = retrieveParameterProfile(type, version, role);
            this.imageProperties = retrieveImageProperties(role, type);
            if (imageProperties.getInternalPort() != null) {
                this.port = imageProperties.getInternalPort();
            }
            this.transportType = transportType;
            if (!image.getLabels()
                    .containsKey(TlsImageLabels.ADDITIONAL_BUILD_FLAGS.getLabelName())) {
                this.additionalBuildFlags = "";
            } else {
                this.additionalBuildFlags =
                        image.getLabels().get(TlsImageLabels.ADDITIONAL_BUILD_FLAGS.getLabelName());
            }
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

        public T containerName(String value) {
            containerName = value;
            return (T) this;
        }

        public T additionalBuildFlags(String value) {
            additionalBuildFlags = value;
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

        public T cmd(String... value) {
            cmd = value;
            return (T) this;
        }

        public T containerExposedPorts(List<ExposedPort> value) {
            containerExposedPorts = value;
            return (T) this;
        }

        public void pull(ConnectionRole connectionRole) {
            // only pull if image does not exist
            Map<String, String> labels = new HashMap<>();
            labels.put(
                    TlsImageLabels.IMPLEMENTATION.getLabelName(),
                    profile.getType().name().toLowerCase());
            labels.put(TlsImageLabels.VERSION.getLabelName(), version);
            labels.put(
                    TlsImageLabels.CONNECTION_ROLE.getLabelName(),
                    connectionRole.toString().toLowerCase());
            Optional<Image> image =
                    DOCKER.listImagesCmd().withLabelFilter(labels).exec().stream().findFirst();
            if (image.isPresent()) {
                LOGGER.warn("Not pulling image, image already exists!");
                return;
            }

            String username = DockerClientManager.getDockerServerUsername();
            String password = DockerClientManager.getDockerServerPassword();
            if (username == null || password == null) {
                LOGGER.warn(
                        "Username or Password for private Docker repository not set. Set in DockerClientManager");
                return;
            }

            Runtime runtime = Runtime.getRuntime();
            try {
                // we import docker-java to handle this but I just couldnt find a way to make
                // docker-java do this
                // its not documented and Im pretty sure that it does not fully work as intended so
                // we go with this
                // for now
                String loginCommand =
                        "docker login -u "
                                + DockerClientManager.getDockerServerUsername()
                                + " -p "
                                + DockerClientManager.getDockerServerPassword()
                                + " "
                                + REPOSITORY_LOCATION;
                String pullCommand =
                        "docker pull "
                                + DOCKER_LIBRARY
                                + profile.getType().name().toLowerCase()
                                + "-"
                                + connectionRole.toString().toLowerCase()
                                + ":"
                                + version;
                String logoutCommand = "docker logout";

                executeCommand(runtime, loginCommand, TIMEOUT);
                executeCommand(runtime, pullCommand, TIMEOUT);
                executeCommand(runtime, logoutCommand, TIMEOUT);
            } catch (IOException | InterruptedException ex) {
                LOGGER.warn("Could not launch command line argument for pulling docker image");
                throw new RuntimeException(ex);
            }
        }

        public abstract DockerTlsInstance build() throws DockerException, InterruptedException;
    }

    public static class TlsClientInstanceBuilder
            extends TlsInstanceBuilder<TlsClientInstanceBuilder> {

        protected boolean connectOnStartup = true;

        public TlsClientInstanceBuilder(
                TlsImplementationType type, String version, TransportType transportType) {
            super(type, version, ConnectionRole.CLIENT, transportType);
        }

        public TlsClientInstanceBuilder(Image image, TransportType transportType) {
            super(image, transportType);
        }

        public TlsClientInstanceBuilder pull() {
            super.pull(ConnectionRole.CLIENT);
            return this;
        }

        @Override
        public DockerTlsClientInstance build() throws DockerException, InterruptedException {
            return new DockerTlsClientInstance(
                    image,
                    containerName,
                    profile,
                    imageProperties,
                    version,
                    additionalBuildFlags,
                    autoRemove,
                    new HostInfo(ip, hostname, port, transportType),
                    additionalParameters,
                    parallelize,
                    insecureConnection,
                    connectOnStartup,
                    hostConfigHook,
                    cmd,
                    containerExposedPorts);
        }

        public TlsClientInstanceBuilder connectOnStartup(boolean value) {
            connectOnStartup = value;
            return this;
        }
    }

    public static class TlsServerInstanceBuilder
            extends TlsInstanceBuilder<TlsServerInstanceBuilder> {

        public TlsServerInstanceBuilder(
                TlsImplementationType type, String version, TransportType transportType) {
            super(type, version, ConnectionRole.SERVER, transportType);
        }

        public TlsServerInstanceBuilder(Image image, TransportType transportType) {
            super(image, transportType);
        }

        public TlsServerInstanceBuilder pull() {
            super.pull(ConnectionRole.SERVER);
            return this;
        }

        @Override
        public DockerTlsServerInstance build() throws DockerException, InterruptedException {
            return new DockerTlsServerInstance(
                    image,
                    containerName,
                    profile,
                    imageProperties,
                    version,
                    additionalBuildFlags,
                    autoRemove,
                    new HostInfo(ip, hostname, port, transportType),
                    additionalParameters,
                    parallelize,
                    insecureConnection,
                    hostConfigHook,
                    cmd,
                    containerExposedPorts);
        }
    }

    public static TlsClientInstanceBuilder getTlsClientBuilder(
            TlsImplementationType type, String version) {
        return new TlsClientInstanceBuilder(type, version, TransportType.TCP);
    }

    public static TlsClientInstanceBuilder getDTlsClientBuilder(
            TlsImplementationType type, String version) {
        return new TlsClientInstanceBuilder(type, version, TransportType.UDP);
    }

    public static TlsServerInstanceBuilder getTlsServerBuilder(
            TlsImplementationType type, String version) {
        return new TlsServerInstanceBuilder(type, version, TransportType.TCP);
    }

    public static TlsServerInstanceBuilder getDTlsServerBuilder(
            TlsImplementationType type, String version) {
        return new TlsServerInstanceBuilder(type, version, TransportType.UDP);
    }

    public static boolean clientExists(TlsImplementationType type, String version) {
        return checkExists(type, version, ConnectionRole.CLIENT);
    }

    public static boolean serverExists(TlsImplementationType type, String version) {
        return checkExists(type, version, ConnectionRole.SERVER);
    }

    private static boolean checkExists(
            TlsImplementationType type, String version, ConnectionRole role) {
        return PropertyManager.instance().getProperties(role, type) != null
                && ParameterProfileManager.instance().getProfile(type, version, role) != null;
    }

    public static ImageProperties retrieveImageProperties(
            ConnectionRole role, TlsImplementationType type) throws PropertyNotFoundException {
        ImageProperties properties = PropertyManager.instance().getProperties(role, type);
        if (properties == null) {
            throw new PropertyNotFoundException(
                    "Could not find a Property for " + role.name() + ": " + type.name());
        }
        return properties;
    }

    public static ParameterProfile retrieveParameterProfile(
            TlsImplementationType type, String version, ConnectionRole role)
            throws DefaultProfileNotFoundException {
        ParameterProfile profile =
                ParameterProfileManager.instance().getProfile(type, version, role);
        if (profile == null) {
            throw new DefaultProfileNotFoundException(
                    "Could not find a Profile for "
                            + role.name()
                            + ": "
                            + type.name()
                            + ":"
                            + version);
        }
        return profile;
    }

    public static List<String> getAvailableVersions(
            ConnectionRole role, TlsImplementationType type) {
        List<String> versionList = new LinkedList<>();
        Map<String, String> labels = new HashMap<>();
        labels.put(TlsImageLabels.IMPLEMENTATION.getLabelName(), type.name().toLowerCase());
        labels.put(TlsImageLabels.CONNECTION_ROLE.getLabelName(), role.toString().toLowerCase());
        List<Image> serverImageList =
                DOCKER.listImagesCmd().withLabelFilter(labels).withDanglingFilter(false).exec();
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
        return DOCKER.listImagesCmd()
                .withLabelFilter(TlsImageLabels.IMPLEMENTATION.getLabelName())
                .withDanglingFilter(false)
                .exec();
    }

    public static Image getMatchingImage(
            List<Image> images,
            TlsImplementationType type,
            String version,
            String additionalBuildFlags,
            ConnectionRole role) {
        return images.stream()
                .filter(
                        image ->
                                version.equals(
                                        image.getLabels()
                                                .get(TlsImageLabels.VERSION.getLabelName())))
                .filter(
                        image ->
                                type.name()
                                        .toLowerCase()
                                        .equals(
                                                image.getLabels()
                                                        .get(
                                                                TlsImageLabels.IMPLEMENTATION
                                                                        .getLabelName())))
                .filter(
                        image ->
                                role.name()
                                        .toLowerCase()
                                        .equals(
                                                image.getLabels()
                                                        .get(
                                                                TlsImageLabels.CONNECTION_ROLE
                                                                        .getLabelName())))
                .filter(
                        image ->
                                additionalBuildFlags.equals(
                                                image.getLabels()
                                                        .get(
                                                                TlsImageLabels
                                                                        .ADDITIONAL_BUILD_FLAGS
                                                                        .getLabelName()))
                                        || (DockerBuilder.NO_ADDITIONAL_BUILDFLAGS.equals(
                                                        additionalBuildFlags)
                                                && image.getLabels()
                                                                .get(
                                                                        TlsImageLabels
                                                                                .ADDITIONAL_BUILD_FLAGS
                                                                                .getLabelName())
                                                        == null))
                .findFirst()
                .orElse(null);
    }

    private static void executeCommand(Runtime runtime, String command, int timeout)
            throws InterruptedException, IOException {
        Process process = runtime.exec(command);
        process.waitFor(timeout, TimeUnit.MINUTES);
        if (process.exitValue() != 0) {
            String readLine;
            StringBuilder output = new StringBuilder();
            BufferedReader processOutputReader =
                    new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((readLine = processOutputReader.readLine()) != null) {
                output.append(readLine);
            }
            LOGGER.warn(
                    "Pulling docker commands failed with exit code "
                            + process.exitValue()
                            + "\nSTDERR: "
                            + output);
            throw new IOException(output.toString());
        }
    }
}
