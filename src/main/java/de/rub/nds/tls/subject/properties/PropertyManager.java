package de.rub.nds.tls.subject.properties;

import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.TlsImplementationType;
import de.rub.nds.tls.subject.exceptions.PropertyNotFoundException;
import java.util.LinkedList;
import java.util.List;

public class PropertyManager {

    private final List<ImageProperties> imagePropertyList;

    private static class Const {

        static final String CERT_KEY_PEM = "/cert/rsa2048key.pem";
        static final String CERT_CERT_PEM = "/cert/rsa2048cert.pem";
        static final String CERT_COMBINED_PEM = "/cert/rsa2048combined.pem";
        static final String RUST_TEST_CA_KEY = "/cert/test-ca/rsa/end.rsa";
        static final String RUST_TEST_CA_FULLCHAIN = "/cert/test-ca/rsa/end.fullchain";
        static final String CA_CERT = "/cert/ca.pem";
    }

    public PropertyManager() {
        imagePropertyList = new LinkedList<>();

        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.BEARSSL, "0.6", null, false));
        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.BORINGSSL, "master", null, false));
        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.BOTAN, "2.14.0", null, false));
        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.FIREFOX, "61.0.2", null, false));
        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.GNUTLS, "3.6.14", Const.CA_CERT, false));
        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.LIBRESSL, "3.2.0", Const.CA_CERT, false));
        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.MATRIXSSL, "4.2.2", null, true));
        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.MBEDTLS, "2.16.6", Const.CA_CERT, false));
        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.NSS, "3.54", Const.CA_CERT, false));
        //imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.OCAMLTLS, "0.8.0", null, false));
        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.OPENSSL, "1.1.1g", null, false));
        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.RUSTLS, "0.17.0", Const.CA_CERT, false));
        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.S2N, "0.10.5", Const.CA_CERT, false));
        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.TLSLITE_NG, "0.8.0-alpha38", Const.CA_CERT, false));
        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.WOLFSSL, "4.4.0-stable", Const.CA_CERT, false));

        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.BEARSSL, "0.6", 4433, Const.CERT_KEY_PEM, Const.CERT_CERT_PEM, null));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.BORINGSSL, "master", 4430, Const.CERT_KEY_PEM, Const.CERT_CERT_PEM, Const.CERT_COMBINED_PEM));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.BOTAN, "2.14.0", 4433, Const.CERT_KEY_PEM, Const.CERT_CERT_PEM, null));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.BOUNCYCASTLE, "1.58", 4433, "/cert/keys.jks", "/cert/keys.jks", null));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.DAMNVULNERABLEOPENSSL, "1.0", 4433, Const.CERT_KEY_PEM, Const.CERT_CERT_PEM, null));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.GNUTLS, "3.6.14", 5556, Const.CERT_KEY_PEM, Const.CERT_CERT_PEM, null));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.JSSE, "9.0.4-12_bc_1_59", 4433, "/cert/keys.jks", "/cert/keys.jks", null));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.LIBRESSL, "3.2.0", 4433, Const.RUST_TEST_CA_KEY, Const.RUST_TEST_CA_FULLCHAIN, null));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.MATRIXSSL, "4.2.2", 4433, Const.CERT_KEY_PEM, Const.CERT_CERT_PEM, null));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.MBEDTLS, "2.16.6", 4433, Const.CERT_KEY_PEM, Const.CERT_CERT_PEM, null));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.NSS, "3.54", 4430, "cert", "/cert/db/", null));
        //imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.OCAMLTLS, ...
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.OPENSSL, "1.1.1g", 4433, Const.CERT_KEY_PEM, Const.CERT_CERT_PEM, null));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.RUSTLS, "0.17.0", 443, Const.CERT_KEY_PEM, Const.CERT_CERT_PEM, null));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.S2N, "0.10.5", 4430, Const.CERT_KEY_PEM, Const.CERT_CERT_PEM, null));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.TLSLITE_NG, "0.8.0-alpha38", 4433, Const.CERT_KEY_PEM, Const.CERT_CERT_PEM, null));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.WOLFSSL, "4.4.0-stable", 11111, Const.CERT_KEY_PEM, Const.CERT_CERT_PEM, null));
    }

    public ImageProperties getProperties(ConnectionRole role, TlsImplementationType type) {
        for (ImageProperties properties : imagePropertyList) {
            if (properties.getRole().equals(role)) {
                if (properties.getType().equals(type)) {
                    return properties;
                }
            }
        }
        throw new PropertyNotFoundException("No " + role.name() + " properties found for: " + type.name());
    }
}
