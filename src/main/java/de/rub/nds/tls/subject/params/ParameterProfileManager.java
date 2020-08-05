package de.rub.nds.tls.subject.params;

import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.TlsImplementationType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ParameterProfileManager {

    static final Logger LOGGER = LogManager.getLogger(ParameterProfileSerializer.class.getName());

    private static final String RESOURCE_PATH = "/profiles/";

    private final List<ParameterProfile> defaultClientProfileList;

    private final List<ParameterProfile> allProfileList;

    private final List<ParameterProfile> defaultServerProfileList;

    public ParameterProfileManager() {
        defaultServerProfileList = new LinkedList<>();
        defaultClientProfileList = new LinkedList<>();
        allProfileList = new LinkedList<>();

        for (ConnectionRole role : ConnectionRole.values()) {
            try {
                for (String filename : getResourceFiles(RESOURCE_PATH + role.name().toLowerCase() + "/")) {
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

        for (TlsImplementationType type
                : TlsImplementationType.values()) {
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

    private List<String> getResourceFiles(String path) throws IOException {
        Reflections reflections = new Reflections(null, new ResourcesScanner());
        Set<String> resourceList = reflections.getResources(Pattern.compile(".*\\.profile")).parallelStream().map(x -> new File(x).getName()).collect(Collectors.toSet());
        return new ArrayList<>(resourceList);
    }

    private ParameterProfile tryLoadProfile(ConnectionRole role, String filename) {
        try {
            InputStream stream = ParameterProfileManager.class
                    .getResourceAsStream(RESOURCE_PATH + role.name().toLowerCase() + "/" + filename);
            return ParameterProfileSerializer.read(stream);
        } catch (IOException | JAXBException | XMLStreamException E) {
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
