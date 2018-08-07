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
        static final String RUST_TEST_CA_KEY = "/cert/test-ca/rsa/end.rsa";
        static final String RUST_TEST_CA_FULLCHAIN = "/cert/test-ca/rsa/end.fullchain";
    }

    public PropertyManager() {
        imagePropertyList = new LinkedList<>();
        
        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.BEARSSL, "0.5"));
        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.BORINGSSL, "master"));
        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.BOTAN, "2.5"));
        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.FIREFOX, "61.0.1"));
        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.GNUTLS, "3.5.16"));
        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.LIBRESSL, "2.6.3"));
        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.MATRIXSSL, "3.9.3"));
        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.MBED, "2.6.0"));
        //imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.OCAML_TLS, ""));
        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.OPENSSL, "1.1.0f"));
        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.S2N, "latest"));
        imagePropertyList.add(new ImageProperties(ConnectionRole.CLIENT, TlsImplementationType.WOLFSSL, "3.12.2-stable"));
        
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.BEARSSL, "0.5", 4433, Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.BORINGSSL, "master", 4430, Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.BOTAN, "2.5", 443, Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.BOUNCYCASTLE, "1.58", 4433, "/cert/keys.jks", "/cert/keys.jks"));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.DAMNVULNERABLEOPENSSL, "1.0", 4433, Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.GNUTLS, "3.5.16", 5556, Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.JSSE, "openjdk:8u162-jre-slim-bc-1-59", 4433, "/cert/keys.jks", "/cert/keys.jks"));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.LIBRESSL, "2.6.3", 4433, Const.RUST_TEST_CA_KEY, Const.RUST_TEST_CA_FULLCHAIN));
        //imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.MATRIXSSL, ...
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.MBED, "2.6.0", 4430, Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.NSS, "", 4430, "/cert/db/", "cert"));
        //imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.OCAML_TLS, ...
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.OPENSSL, "1.1.0f", 4433, Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.RUSTLS, "", 443, Const.RUST_TEST_CA_KEY, Const.RUST_TEST_CA_FULLCHAIN));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.S2N, "latest", 4430, Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        imagePropertyList.add(new ImageProperties(ConnectionRole.SERVER, TlsImplementationType.WOLFSSL, "3.12.2-stable", 11111, Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
    }

    public ImageProperties getProperties(ConnectionRole role, TlsImplementationType type) {
        for (ImageProperties properties : imagePropertyList) {
            if (properties.getRole().equals(role)) {
                if (properties.getType() == type) {
                    return properties;
                }
            }
        }
        throw new PropertyNotFoundException("No " + role.name() + " properties found for: " + type.name());
    }
}
