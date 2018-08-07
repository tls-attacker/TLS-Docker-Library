package de.rub.nds.tls.subject.params;

import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.TlsImplementationType;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParameterProfileManager {

    static final Logger LOGGER = LogManager.getLogger(ParameterProfileSerializer.class.getName());

    private static final String RESOURCE_PATH = "/profiles/";
    
    private static final String PROFILES_PATH = "src/main/resources/profiles/";

    private final List<ParameterProfile> defaultClientProfileList;
    
    private final List<ParameterProfile> otherClientProfileList;

    private final List<ParameterProfile> defaultServerProfileList;

    public ParameterProfileManager() {
        defaultServerProfileList = new LinkedList<>();
        defaultClientProfileList = new LinkedList<>();
        otherClientProfileList = new LinkedList<>();
        for (TlsImplementationType type : TlsImplementationType.values()) {
            ParameterProfile profile = tryLoadDefaultProfile(type, ConnectionRole.CLIENT);
            if (profile != null) {
                LOGGER.debug("Loaded:" + profile.getName() + " : " + profile.getRole().name());
                defaultClientProfileList.add(profile);
            }
        }
        File dir = new File(PROFILES_PATH + ConnectionRole.CLIENT.name().toLowerCase() + "/");
        for (TlsImplementationType type : TlsImplementationType.values()) {
            String pattern = type.name().toLowerCase() + ".+\\.profile";
            FileFilter filter = new RegexFileFilter(pattern);
            File[] files = dir.listFiles(filter);
            if (files!=null) {
                for (File file : files) {
                    ParameterProfile profile = tryLoadOtherProfile(type, ConnectionRole.CLIENT, file.getName());
                    if (profile != null) {
                        LOGGER.debug("Loaded:" + profile.getName() + " : " + profile.getRole().name() + " - " + profile.getDescription());
                        otherClientProfileList.add(profile);
                    }
                }
            }
        }
        for (TlsImplementationType type : TlsImplementationType.values()) {
            ParameterProfile profile = tryLoadDefaultProfile(type, ConnectionRole.SERVER);
            if (profile != null) {
                LOGGER.debug("Loaded:" + profile.getName() + " : " + profile.getRole().name());
                defaultServerProfileList.add(profile);
            }
        }
        //Other profiles for server here
    }

    private ParameterProfile tryLoadDefaultProfile(TlsImplementationType type, ConnectionRole role) {
        try {
            InputStream stream = ParameterProfileManager.class.getResourceAsStream(RESOURCE_PATH + role.name().toLowerCase() + "/" + type.name().toLowerCase() + ".profile");
            return ParameterProfileSerializer.read(stream);
        } catch (IOException | JAXBException | XMLStreamException E) {
            LOGGER.debug("Could not find default ParameterProfile for: " + type.name() + ": " + role.name());
            LOGGER.trace(E);
            return null;
        }
    }
    
    private ParameterProfile tryLoadOtherProfile(TlsImplementationType type, ConnectionRole role, String filename) {
        try {
            InputStream stream = ParameterProfileManager.class.getResourceAsStream(RESOURCE_PATH + role.name().toLowerCase() + "/" + filename);
            return ParameterProfileSerializer.read(stream);
        } catch (IOException | JAXBException | XMLStreamException E) {
            LOGGER.debug("Could not find other ParameterProfile for: " + type.name() + ": " + role.name());
            LOGGER.trace(E);
            return null;
        }
    }

    public ParameterProfile getDefaultProfile(TlsImplementationType type, ConnectionRole role) {
        if (null == role) {
            throw new IllegalArgumentException("Unknown ConnectionRole: " + role.name());
        } else {
            switch (role) {
                case CLIENT:
                    for (ParameterProfile profile : defaultClientProfileList) {
                        if (profile.getType() == type) {
                            return profile;
                        }
                    }
                    return null;
                case SERVER:
                    for (ParameterProfile profile : defaultServerProfileList) {
                        if (profile.getType() == type) {
                            return profile;
                        }
                    }
                    return null;
                default:
                    throw new IllegalArgumentException("Unknown ConnectionRole: " + role.name());
            }
        }
    }
    
    public List<ParameterProfile> getOtherProfiles(TlsImplementationType type, ConnectionRole role) {
        if (null == role) {
            throw new IllegalArgumentException("Unknown ConnectionRole: " + role.name());
        } else {
            switch (role) {
                case CLIENT:
                    List<ParameterProfile> profileList = new LinkedList<>();
                    for (ParameterProfile profile : otherClientProfileList) {
                        if (profile.getType() == type) {
                            profileList.add(profile);
                        }
                    }
                    return profileList;
                case SERVER:
                    //Other profiles for server here
                default:
                    throw new IllegalArgumentException("Unknown ConnectionRole: " + role.name());
            }
        }
    }
}
