package de.rub.nds.tls.subject.properties;

import de.rub.nds.tls.subject.TlsImplementationType;
import de.rub.nds.tls.subject.exceptions.PropertyNotFoundException;
import java.util.LinkedList;
import java.util.List;

public class PropertyManager {

    private final List<ImageProperties> propertyList;

    private static class Const {

        static final String CERT_KEY_PEM = "/cert/rsa2048key.pem";
        static final String CERT_CERT_PEM = "/cert/rsa2048cert.pem";
        static final String RUST_TEST_CA_KEY = "/cert/test-ca/rsa/end.rsa";
        static final String RUST_TEST_CA_FULLCHAIN = "/cert/test-ca/rsa/end.fullchain";
    }

    public PropertyManager() {
        propertyList = new LinkedList<>();
        propertyList.add(new ImageProperties(TlsImplementationType.MBED, 4430, "", Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        propertyList.add(new ImageProperties(TlsImplementationType.OPENSSL, 4433, "", Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        propertyList.add(new ImageProperties(TlsImplementationType.DAMN_VULNERABLE_OPENSSL, 4433, "", Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        propertyList.add(new ImageProperties(TlsImplementationType.NSS, 4430, "", "/cert/db/", "cert"));
        propertyList.add(new ImageProperties(TlsImplementationType.GNUTLS, 5556, "", Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        propertyList.add(new ImageProperties(TlsImplementationType.BORINGSSL, 4430, "", Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        propertyList.add(new ImageProperties(TlsImplementationType.S2N, 4430, "", Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        propertyList.add(new ImageProperties(TlsImplementationType.LIBRESSL, 4433, "", Const.RUST_TEST_CA_KEY, Const.RUST_TEST_CA_FULLCHAIN));
        propertyList.add(new ImageProperties(TlsImplementationType.BEARSSL, 4433, "", Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        propertyList.add(new ImageProperties(TlsImplementationType.BOTAN, 443, "", Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        propertyList.add(new ImageProperties(TlsImplementationType.WOLFSSL, 11111, "", Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        propertyList.add(new ImageProperties(TlsImplementationType.RUSTLS, 443, "", Const.RUST_TEST_CA_KEY, Const.RUST_TEST_CA_FULLCHAIN));
        propertyList.add(new ImageProperties(TlsImplementationType.BOUNCYCASTLE, 4433, "", "/cert/keys.jks", "/cert/keys.jks"));
    }

    public ImageProperties getProperties(TlsImplementationType type) {
        for (ImageProperties properties : propertyList) {
            if (properties.getType() == type) {
                return properties;
            }
        }
        throw new PropertyNotFoundException("No properties found for: " + type.name());
    }
}
