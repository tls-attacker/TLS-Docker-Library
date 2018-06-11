package de.rub.nds.tls.subject.properties;

import de.rub.nds.tls.subject.TlsImplementationType;
import de.rub.nds.tls.subject.exceptions.PropertyNotFoundException;
import java.util.LinkedList;
import java.util.List;

public class ClientPropertyManager {

    private final List<ClientImageProperties> propertyList;

    public ClientPropertyManager() {
        propertyList = new LinkedList<>();
        propertyList.add(new ClientImageProperties(TlsImplementationType.BEARSSL, "0.5"));
        propertyList.add(new ClientImageProperties(TlsImplementationType.BORINGSSL, "master"));
        propertyList.add(new ClientImageProperties(TlsImplementationType.BOTAN, "2.5"));
        propertyList.add(new ClientImageProperties(TlsImplementationType.GNUTLS, "3.5.16"));
        propertyList.add(new ClientImageProperties(TlsImplementationType.LIBRESSL, ""));
        propertyList.add(new ClientImageProperties(TlsImplementationType.MATRIXSSL, ""));
        propertyList.add(new ClientImageProperties(TlsImplementationType.MBED, "2.6.0"));
        //propertyList.add(new ClientImageProperties(TlsImplementationType.OCAML_TLS, ""));
        propertyList.add(new ClientImageProperties(TlsImplementationType.OPENSSL, "1.1.0f"));
        propertyList.add(new ClientImageProperties(TlsImplementationType.S2N, ""));
        propertyList.add(new ClientImageProperties(TlsImplementationType.WOLFSSL, "3.12.2-stable"));
    }

    public ClientImageProperties getProperties(TlsImplementationType type) {
        for (ClientImageProperties properties : propertyList) {
            if (properties.getType() == type) {
                return properties;
            }
        }
        throw new PropertyNotFoundException("No client properties found for: " + type.name());
    }
}
