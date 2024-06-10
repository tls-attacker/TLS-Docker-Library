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
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
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
    public static final String IMAGES_RESOURCE_DIRECTORY = "/images";
    public static final String JSON_BUILD_INFO_FILENAME = "build.json";
    private static final Logger LOGGER = LogManager.getLogger();

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
        if (buildFlags.isEmpty()) {
            return "";
        }
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No SHA-256 instance available to create parameter tag");
        }
        messageDigest.update(
                (library.name() + version + connectionRole.name() + buildFlags).getBytes());
        String hashString = DatatypeConverter.printHexBinary(messageDigest.digest()).toLowerCase();
        hashString = hashString.substring(0, Math.min(16, hashString.length()));
        return "-flags-" + hashString;
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
            DOCKER.buildImageCmd()
                    .withDockerfile(dockerfilePath.toFile())
                    .withBuildArg(BUILD_FLAGS_ARGUMENT, buildFlags)
                    .withBuildArg(VERSION_ARGUMENT, dockerfileArguments.getVersionBuildArgument())
                    .exec(new BuildImageResultCallback())
                    .awaitImageId();
            tagBuiltImages(library, version, connectionRole, buildFlags, previouslyBuiltImages);
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
            String builtFlagsTag =
                    getBuildFlagParameterTag(library, version, ConnectionRole.CLIENT, buildFlags);
            DOCKER.tagImageCmd(
                            matchingClientImages.get(0).getId(),
                            library.name().toLowerCase()
                                    + "-"
                                    + ConnectionRole.CLIENT.name().toLowerCase(),
                            version + builtFlagsTag)
                    .exec();
            tagged++;
        }
        if (!matchingServerImages.isEmpty()
                && !previouslyBuiltImages.contains(matchingServerImages.get(0))) {
            String builtFlagsTag =
                    getBuildFlagParameterTag(library, version, ConnectionRole.SERVER, buildFlags);
            DOCKER.tagImageCmd(
                            matchingClientImages.get(0).getId(),
                            library.name().toLowerCase()
                                    + "-"
                                    + ConnectionRole.SERVER.name().toLowerCase(),
                            version + builtFlagsTag)
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
                        .withFilter("name", Arrays.asList("cert-data"))
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
                && labels.get(TlsImageLabels.ADDITIONAL_BUILD_FLAGS.getLabelName()).isEmpty()) {
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
            jsonBuildData = objectMapper.readValue(jsonFile.toFile(), JsonBuildData.class);
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
            Path imagesPath = Paths.get(DockerBuilder.class.getResource("/images").toURI());

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
}
