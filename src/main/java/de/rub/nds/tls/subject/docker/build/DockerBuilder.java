/*
 * TLS-Docker-Library - A collection of open source TLS clients and servers
 *
 * Copyright 2017-2024 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tls.subject.docker.build;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.InspectVolumeResponse;
import com.github.dockerjava.api.model.Image;
import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.TlsImplementationType;
import de.rub.nds.tls.subject.constants.TlsImageLabels;
import de.rub.nds.tls.subject.docker.DockerClientManager;
import de.rub.nds.tls.subject.docker.build.exception.VersionNotListedException;
import de.rub.nds.tls.subject.exceptions.CertVolumeNotFoundException;
import jakarta.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides an API to build TLS libraries registered as JSON files. This class does not replace the
 * build script (yet).
 */
public class DockerBuilder {
    private static final String BUILD_FLAGS_ARGUMENT = "BUILD_FLAGS";
    private static final String VERSION_ARGUMENT = "VERSION";
    public static final String CERTIFICATE_VOLUME_NAME = "cert-data";
    public static final String IMAGES_RESOURCE_DIRECTORY = "/images";
    public static final String NO_ADDITIONAL_BUILDFLAGS = "";
    public static final String JSON_BUILD_INFO_FILENAME = "build.json";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<String, File> temporaryDockerfileMap = new HashMap<>();

    private final Map<TlsImplementationType, Path> libraryImageDirectories;
    private final Map<TlsImplementationType, JsonBuildData> knownBuildableLibraries;

    private static final DockerClient DOCKER = DockerClientManager.getDockerClient();

    public DockerBuilder() {
        libraryImageDirectories = readLibraryDirectories();
        knownBuildableLibraries = getBuildInformationMap(libraryImageDirectories);
    }

    /**
     * When there is a large number of docker images, it may take some time to fetch the list of
     * images. Hence, we cache the list and only update when necessary.
     */
    public void updateLocallyAvailableBuilds() {}

    public static String getBuildFlagParameterTag(
            TlsImplementationType library,
            String version,
            ConnectionRole connectionRole,
            String buildFlags) {
        if (buildFlags.equals(NO_ADDITIONAL_BUILDFLAGS)) {
            return NO_ADDITIONAL_BUILDFLAGS;
        }
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No SHA-256 instance available to create parameter tag");
        }
        messageDigest.update(
                (TlsImageLabels.IMPLEMENTATION.getLabelName()
                                + library.name()
                                + TlsImageLabels.VERSION.getLabelName()
                                + version
                                + TlsImageLabels.CONNECTION_ROLE.getLabelName()
                                + connectionRole.name()
                                + TlsImageLabels.ADDITIONAL_BUILD_FLAGS.getLabelName()
                                + buildFlags)
                        .getBytes());
        String hashString = DatatypeConverter.printHexBinary(messageDigest.digest()).toLowerCase();
        hashString = hashString.substring(0, Math.min(16, hashString.length()));
        return "-flags-" + hashString;
    }

    /**
     * Returns the default repo and tag used when building images with the TLS-Docker-Library in
     * docker's typical format (repo:tag).
     *
     * @param library The TLS library to build.
     * @param version The verison to build.
     * @param connectionRole The connection role of the image.
     * @param buildFlags The flags to apply during build.
     * @return The repo:tag string
     */
    public static String getDefaultRepoAndTag(
            TlsImplementationType library,
            String version,
            ConnectionRole connectionRole,
            String buildFlags) {
        return getDefaultRepo(library, connectionRole)
                + ":"
                + getDefaultTag(library, version, connectionRole, buildFlags);
    }

    public static String getDefaultRepo(
            TlsImplementationType library, ConnectionRole connectionRole) {
        return library.name().toLowerCase() + "-" + connectionRole.name().toLowerCase();
    }

    public static String getDefaultTag(
            TlsImplementationType library,
            String version,
            ConnectionRole connectionRole,
            String buildFlags) {
        return version + getBuildFlagParameterTag(library, version, connectionRole, buildFlags);
    }

    /**
     * Attempts to build a library and version docker image using the provided parameters. Requires
     * that the setup script has already been run on the machine executing this code.
     *
     * @param library The TLS library to build
     * @param version The version of the TLS library to build
     * @param buildFlags Flags to append to the build process. Must be supported by the relevant
     *     dockerfile.
     * @return The newly built image or a previously built image matching the defined parameters
     */
    public Image buildLibraryImage(
            TlsImplementationType library,
            String version,
            ConnectionRole connectionRole,
            String buildFlags)
            throws VersionNotListedException {
        List<Image> previouslyBuiltImages = DOCKER.listImagesCmd().exec();
        Image builtImage = getBuiltImage(library, version, connectionRole, buildFlags);
        if (builtImage == null) {
            DockerfileArguments dockerfileArguments =
                    knownBuildableLibraries.get(library).getDockerfileArgumentsForVersion(version);
            if (dockerfileArguments == null) {
                throw new VersionNotListedException();
            }
            Path dockerfilePath =
                    libraryImageDirectories
                            .get(library)
                            .resolve(dockerfileArguments.getDockerfileName());
            try {
                File dockerfile =
                        prepareDockerfile(Files.newInputStream(dockerfilePath), library, version);
                DOCKER.buildImageCmd()
                        .withDockerfile(dockerfile)
                        .withBuildArg(BUILD_FLAGS_ARGUMENT, buildFlags)
                        .withBuildArg(
                                VERSION_ARGUMENT, dockerfileArguments.getVersionBuildArgument())
                        .exec(new BuildImageResultCallback())
                        .awaitImageId();
                tagBuiltImages(library, version, connectionRole, buildFlags, previouslyBuiltImages);
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
        return builtImage;
    }

    /**
     * Our dockerfiles usually result in a build image and in an optimized client and server image.
     * Unfortunately, the buildImageCmd call only gives us access to one of these images. Hence, we
     * try to find the new images and tag them accordingly after the build.
     *
     * @param library The TLS library built
     * @param version The version built
     * @param connectionRole The connection role built
     * @param buildFlags The additional build flags supplied
     * @param previouslyBuiltImages The list of images available before the current build process
     *     was started
     */
    private static void tagBuiltImages(
            TlsImplementationType library,
            String version,
            ConnectionRole connectionRole,
            String buildFlags,
            List<Image> previouslyBuiltImages) {
        Map<String, String> mandatoryLabelsClient =
                getImageLabels(library, version, ConnectionRole.CLIENT, buildFlags);
        Map<String, String> mandatoryLabelsServer =
                getImageLabels(library, version, ConnectionRole.SERVER, buildFlags);
        List<Image> matchingClientImages =
                DOCKER.listImagesCmd().withLabelFilter(mandatoryLabelsClient).exec();
        List<Image> matchingServerImages =
                DOCKER.listImagesCmd().withLabelFilter(mandatoryLabelsServer).exec();
        int tagged = 0;
        if (!matchingClientImages.isEmpty()
                && !previouslyBuiltImages.contains(matchingClientImages.get(0))) {
            DOCKER.tagImageCmd(
                            matchingClientImages.get(0).getId(),
                            getDefaultRepo(library, ConnectionRole.CLIENT),
                            getDefaultTag(library, version, ConnectionRole.CLIENT, buildFlags))
                    .exec();
            tagged++;
        }
        if (!matchingServerImages.isEmpty()
                && !previouslyBuiltImages.contains(matchingServerImages.get(0))) {
            DOCKER.tagImageCmd(
                            matchingServerImages.get(0).getId(),
                            getDefaultRepo(library, ConnectionRole.SERVER),
                            getDefaultTag(library, version, ConnectionRole.SERVER, buildFlags))
                    .exec();
            tagged++;
        }

        if (tagged == 0) {
            throw new RuntimeException(
                    "Failed to identify tagged images. Images may not match provided parameters.");
        }
    }

    public static boolean imageMatchesLabels(Image image, Map<String, String> labels) {
        Map<String, String> imageLabels = image.getLabels();
        if (labels.isEmpty()) {
            throw new IllegalArgumentException(
                    "Passed empty map of labels. All images would match.");
        }
        for (String labelKey : labels.keySet()) {
            if (imageLabels == null
                    || !imageLabels.containsKey(labelKey)
                    || !imageLabels.get(labelKey).equals(labels.get(labelKey))) {
                return false;
            }
        }
        return labels.size() == imageLabels.keySet().size();
    }

    public static boolean isImageLocallyAvailable(
            TlsImplementationType implementationType,
            String version,
            ConnectionRole connectionRole) {
        Map<String, String> labels =
                getImageLabels(implementationType, version, connectionRole, "");
        Optional<Image> image =
                DOCKER.listImagesCmd().withLabelFilter(labels).exec().stream().findFirst();

        return image.isPresent();
    }

    public static boolean isImageLocallyAvailable(
            TlsImplementationType implementationType,
            String version,
            ConnectionRole connectionRole,
            String buildFlags) {
        return getBuiltImage(implementationType, version, connectionRole, buildFlags) != null;
    }

    public static Image getBuiltImage(
            TlsImplementationType implementationType,
            String version,
            ConnectionRole connectionRole) {
        Map<String, String> labels =
                getImageLabels(implementationType, version, connectionRole, "");
        return getImageWithLabels(labels, true);
    }

    public static Image getBuiltImage(
            TlsImplementationType implementationType,
            String version,
            ConnectionRole connectionRole,
            String buildFlags) {
        Map<String, String> labels =
                getImageLabels(implementationType, version, connectionRole, buildFlags);
        return getImageWithLabels(labels, true);
    }

    public static InspectVolumeResponse getCertDataVolumeInfo() {
        InspectVolumeResponse volumeInfo =
                DOCKER
                        .listVolumesCmd()
                        .withFilter("name", Arrays.asList(CERTIFICATE_VOLUME_NAME))
                        .exec()
                        .getVolumes()
                        .stream()
                        .findFirst()
                        .orElseThrow(CertVolumeNotFoundException::new);
        return volumeInfo;
    }

    public static Image getImageWithLabels(
            Map<String, String> labels, boolean allowMissingEmptyBuildFlags) {
        Optional<Image> image =
                DOCKER.listImagesCmd().withLabelFilter(labels).exec().stream().findFirst();
        if (image.isPresent()) {
            return image.get();
        } else if (allowMissingEmptyBuildFlags
                && labels.containsKey(TlsImageLabels.ADDITIONAL_BUILD_FLAGS.getLabelName())
                && labels.get(TlsImageLabels.ADDITIONAL_BUILD_FLAGS.getLabelName())
                        .equals(NO_ADDITIONAL_BUILDFLAGS)) {
            Map<String, String> reducedLabels = new HashMap<>();
            reducedLabels.putAll(labels);
            reducedLabels.remove(TlsImageLabels.ADDITIONAL_BUILD_FLAGS.getLabelName());
            image =
                    DOCKER.listImagesCmd().withLabelFilter(reducedLabels).exec().stream()
                            .findFirst();
            if (image.isPresent()) {
                return image.get();
            }
        }
        return null;
    }

    public static Map<String, String> getImageLabels(
            TlsImplementationType implementationType,
            String version,
            ConnectionRole connectionRole,
            String buildFlags) {
        Map<String, String> labels = new HashMap<>();
        labels.put(
                TlsImageLabels.IMPLEMENTATION.getLabelName(),
                implementationType.name().toLowerCase());
        labels.put(TlsImageLabels.VERSION.getLabelName(), version);
        labels.put(
                TlsImageLabels.CONNECTION_ROLE.getLabelName(),
                connectionRole.toString().toLowerCase());
        labels.put(TlsImageLabels.ADDITIONAL_BUILD_FLAGS.getLabelName(), buildFlags);
        return labels;
    }

    /**
     * Retrieves the libraries listed in the various json files of our images directory
     *
     * @return Map of known implementations and their versions
     */
    public static Map<TlsImplementationType, JsonBuildData> getBuildInformationMap(
            Map<TlsImplementationType, Path> consideredLibraryImageDirectories) {
        HashMap<TlsImplementationType, JsonBuildData> buildInfoMap = new HashMap<>();
        for (TlsImplementationType library : consideredLibraryImageDirectories.keySet()) {
            JsonBuildData buildInfo =
                    readBuildInformation(consideredLibraryImageDirectories.get(library));
            if (buildInfo != null) {
                buildInfoMap.put(library, buildInfo);
            }
        }
        return buildInfoMap;
    }

    public static JsonBuildData readBuildInformation(Path libraryDirectory) {
        ObjectMapper objectMapper = new ObjectMapper();
        Path jsonFile = libraryDirectory.resolve(JSON_BUILD_INFO_FILENAME);
        JsonBuildData jsonBuildData = null;
        try {
            jsonBuildData =
                    objectMapper.readValue(Files.newInputStream(jsonFile), JsonBuildData.class);
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return jsonBuildData;
    }

    /**
     * Finds library image directories based on the known @{TlsImplementationType}s. Note that this
     * method will not find certain sub-types of libraries, such as python-mbedtls.
     *
     * @return
     */
    public static Map<TlsImplementationType, Path> readLibraryDirectories() {
        HashMap<TlsImplementationType, Path> libraryDirectories = new HashMap<>();
        HashMap<String, TlsImplementationType> directoryNameEnumMap = new HashMap<>();
        Arrays.asList(TlsImplementationType.values()).stream()
                .forEach(type -> directoryNameEnumMap.put(type.name().toLowerCase(), type));
        try {
            URI imagesDirectoryUri = DockerBuilder.class.getResource("/images").toURI();
            Path imagesPath = resolveUriToPath(imagesDirectoryUri);

            // List subdirectories
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(imagesPath)) {
                stream.forEach(
                        entry -> {
                            String lowerCaseName = entry.getFileName().toString().toLowerCase();
                            if (Files.isDirectory(entry)
                                    && directoryNameEnumMap.keySet().contains(lowerCaseName)) {
                                libraryDirectories.put(
                                        directoryNameEnumMap.get(lowerCaseName), entry);
                                LOGGER.debug(
                                        "Added image directory {} for implementation type {}",
                                        lowerCaseName,
                                        directoryNameEnumMap.get(lowerCaseName));
                            }
                        });
            }
        } catch (URISyntaxException | IOException e) {
            LOGGER.error(e);
        }
        return libraryDirectories;
    }

    public Map<TlsImplementationType, JsonBuildData> getKnownBuildableLibraries() {
        return knownBuildableLibraries;
    }

    private static Path resolveUriToPath(URI uri) {
        try {
            if (uri.getScheme().equals("jar")) {
                FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                Path imagesPath = fileSystem.getPath("/images");
                return imagesPath;
            } else {
                Path imagesPath = Paths.get(uri);
                return imagesPath;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * When using the TLS-Docker-Library's jar in a another project, we can not directly turn a Path
     * obtained from the zip filesystem java allocates to a File. Java Docker, however, only accepts
     * File objects when using .withDockerfile(). Hence, we write temporary dockerfiles and pass
     * these files to Java Docker.
     *
     * @param stream The input stream created from a Path
     * @param library The library to build (for caching purposes)
     * @param version The version to build (for caching purposes)
     * @return The temporary dockerfile to use in .withDockerfile()
     * @throws IOException
     */
    private static synchronized File prepareDockerfile(
            InputStream stream, TlsImplementationType library, String version) throws IOException {
        File tempDir = Files.createTempDirectory("temp-docker-build-context").toFile();
        tempDir.deleteOnExit();
        String identifier = library.name().toLowerCase() + "+" + version;
        if (temporaryDockerfileMap.containsKey(identifier)) {
            return temporaryDockerfileMap.get(identifier);
        } else {
            if (stream == null) {
                return null;
            }

            try {
                File tempFile = new File(tempDir, "Dockerfile-" + identifier);

                FileOutputStream out = new FileOutputStream(tempFile);
                byte[] buffer = new byte[1024];

                int bytesRead;
                while ((bytesRead = stream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                out.close();
                temporaryDockerfileMap.put(identifier, tempFile);
                return tempFile;
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
                return null;
            }
        }
    }
}
