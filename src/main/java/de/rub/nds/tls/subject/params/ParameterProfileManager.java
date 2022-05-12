package de.rub.nds.tls.subject.params;


import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.TlsImplementationType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ParameterProfileManager {

    static final Logger LOGGER = LogManager.getLogger(ParameterProfileSerializer.class.getName());

    private static ParameterProfileManager instance;

    public static ParameterProfileManager instance() {
        if (instance == null) {
            instance = new ParameterProfileManager();
        }
        return instance;
    }

    private static final String RESOURCE_PATH = "/profiles/";

    private final List<ParameterProfile> defaultClientProfileList;

    private final List<ParameterProfile> allProfileList;

    private final List<ParameterProfile> defaultServerProfileList;

    protected ParameterProfileManager() {
        defaultServerProfileList = new LinkedList<>();
        defaultClientProfileList = new LinkedList<>();
        allProfileList = new LinkedList<>();

        //Create file system inside the jar for fetching the profiles later
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        URL url = getClass().getResource(RESOURCE_PATH);

        try {
            FileSystem zipfs = FileSystems.newFileSystem(url.toURI(), env);
        } catch (URISyntaxException | IOException e) {
            LOGGER.warn("Problem reading profiles", e);
            e.printStackTrace();
        }

        for (ConnectionRole role : ConnectionRole.values()) {
            try {
                for (String filename : getResourceFiles(url)) {
                    ParameterProfile profile = tryLoadProfile(role, filename);
                    if (profile != null) {
                        LOGGER.debug("Loaded:" + profile.getName() + " : " + profile.getRole().name() + " - " + profile.getDescription());
                        allProfileList.add(profile);
                    }
                }
            } catch (IOException ex) {
                LOGGER.warn("Problem reading profiles", ex);
                ex.printStackTrace();
            }
        }

        for (TlsImplementationType type : TlsImplementationType.values()) {
            ParameterProfile profile = tryLoadProfile(ConnectionRole.SERVER, "" + type.name().toLowerCase() + ".profile");
            if (profile != null) {
                LOGGER.debug("Loaded:" + profile.getName() + " : " + profile.getRole().name());
                defaultServerProfileList.add(profile);
            }
            profile = tryLoadProfile(ConnectionRole.CLIENT, "" + type.name().toLowerCase() + ".profile");
            if (profile != null) {
                LOGGER.debug("Loaded:" + profile.getName() + " : " + profile.getRole().name());
                defaultClientProfileList.add(profile);
            }
        }
    }

    private List<String> getResourceFiles(URL url) throws IOException {
        List<String> resourceList = new ArrayList<>();
        Path pathToResource = null;
        try {
            pathToResource = Paths.get(url.toURI());
            Files.walk(pathToResource, 5).forEach(path1 -> {
                String resource = path1.toString();
                resourceList.add(resource.substring(resource.lastIndexOf("/") + 1, resource.length()));
            });
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>(resourceList);
    }

    private ParameterProfile tryLoadProfile(ConnectionRole role, String filename) {
        try {
            InputStream stream = ParameterProfileManager.class
                    .getResourceAsStream(RESOURCE_PATH + role.name().toLowerCase() + "/" + filename);
            return ParameterProfileSerializer.read(stream);
        } catch (IllegalArgumentException | IOException | JAXBException | XMLStreamException E) {
            LOGGER.debug("Could not find other ParameterProfile for: " + RESOURCE_PATH + role.name().toLowerCase() + "/" + filename + ": " + role.name());
            LOGGER.trace(E);
            return null;
        }
    }

    public ParameterProfile getProfile(TlsImplementationType type, String version, ConnectionRole role) {
        for (ParameterProfile profile : allProfileList) {
            if (profile.getRole() == role && profile.getType() == type) {
                if (profile.getVersionList() != null && !profile.getVersionList().isEmpty()) {
                    for (String versionRegex : profile.getVersionList()) {
                        if (version.matches(versionRegex)) {
                            return profile;
                        }
                    }
                }
            }
        }
        return getDefaultProfile(type, role);
    }

    public ParameterProfile getDefaultProfile(TlsImplementationType type, ConnectionRole role) {
        List<ParameterProfile> profileList;
        if (role == ConnectionRole.CLIENT) {
            profileList = defaultClientProfileList;
        } else if (role == ConnectionRole.SERVER) {
            profileList = defaultServerProfileList;
        } else {
            throw new IllegalArgumentException("Unknown ConnectionRole: " + role);
        }

        for (ParameterProfile profile : profileList) {
            if (profile.getType() == type) {
                return profile;
            }
        }
        return null;
    }
}
