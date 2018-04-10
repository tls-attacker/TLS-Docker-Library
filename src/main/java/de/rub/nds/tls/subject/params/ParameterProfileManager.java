package de.rub.nds.tls.subject.params;

import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.TlsImplementationType;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParameterProfileManager {

    static final Logger LOGGER = LogManager.getLogger(ParameterProfileSerializer.class.getName());

    private static final String RESOURCE_PATH = "/profiles/";

    private final List<ParameterProfile> defaultClientProfileList;

    private final List<ParameterProfile> defaultServerProfileList;

    public ParameterProfileManager() {
        defaultServerProfileList = new LinkedList<>();
        defaultClientProfileList = new LinkedList<>();
        for (TlsImplementationType type : TlsImplementationType.values()) {
            ParameterProfile profile = tryLoadDefaultProfile(type, ConnectionRole.CLIENT);
            if (profile != null) {
                LOGGER.debug("Loaded:" + profile.getName() + " : " + profile.getRole().name());
                defaultClientProfileList.add(profile);
            }
        }
        for (TlsImplementationType type : TlsImplementationType.values()) {
            ParameterProfile profile = tryLoadDefaultProfile(type, ConnectionRole.SERVER);
            if (profile != null) {
                LOGGER.debug("Loaded:" + profile.getName() + " : " + profile.getRole().name());
                defaultServerProfileList.add(profile);
            }
        }
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
}
