package de.rub.nds.tls.subject.properties;

import de.rub.nds.tls.subject.TlsImplementationType;
import de.rub.nds.tls.subject.exceptions.PropertyNotFoundException;
import java.util.LinkedList;
import java.util.List;

public class ServerPropertyManager {

    private final List<ServerImageProperties> propertyList;

    private static class Const {

        static final String CERT_KEY_PEM = "/cert/rsa2048key.pem";
        static final String CERT_CERT_PEM = "/cert/rsa2048cert.pem";
        static final String RUST_TEST_CA_KEY = "/cert/test-ca/rsa/end.rsa";
        static final String RUST_TEST_CA_FULLCHAIN = "/cert/test-ca/rsa/end.fullchain";
    }

    public ServerPropertyManager() {
        propertyList = new LinkedList<>();
        propertyList.add(new ServerImageProperties(TlsImplementationType.BEARSSL, 4433, "0.5", Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        propertyList.add(new ServerImageProperties(TlsImplementationType.BORINGSSL, 4430, "master", Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        propertyList.add(new ServerImageProperties(TlsImplementationType.BOTAN, 443, "2.5", Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        propertyList.add(new ServerImageProperties(TlsImplementationType.BOUNCYCASTLE, 4433, "1.58", "/cert/keys.jks", "/cert/keys.jks"));
        propertyList.add(new ServerImageProperties(TlsImplementationType.DAMNVULNERABLEOPENSSL, 4433, "1.0", Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        propertyList.add(new ServerImageProperties(TlsImplementationType.GNUTLS, 5556, "3.5.16", Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        propertyList.add(new ServerImageProperties(TlsImplementationType.JSSE, 4433, "openjdk:8u162-jre-slim-bc-1-59", "/cert/keys.jks", "/cert/keys.jks"));
        propertyList.add(new ServerImageProperties(TlsImplementationType.LIBRESSL, 4433, "2.6.3", Const.RUST_TEST_CA_KEY, Const.RUST_TEST_CA_FULLCHAIN));
        //propertyList.add(new ServerImageProperties(TlsImplementationType.MATRIXSSL, ...
        propertyList.add(new ServerImageProperties(TlsImplementationType.MBED, 4430, "2.6.0", Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        propertyList.add(new ServerImageProperties(TlsImplementationType.NSS, 4430, "", "/cert/db/", "cert"));
        //propertyList.add(new ServerImageProperties(TlsImplementationType.OCAML_TLS, ...
        propertyList.add(new ServerImageProperties(TlsImplementationType.OPENSSL, 4433, "1.1.0f", Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        propertyList.add(new ServerImageProperties(TlsImplementationType.RUSTLS, 443, "", Const.RUST_TEST_CA_KEY, Const.RUST_TEST_CA_FULLCHAIN));
        propertyList.add(new ServerImageProperties(TlsImplementationType.S2N, 4430, "latest", Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
        propertyList.add(new ServerImageProperties(TlsImplementationType.WOLFSSL, 11111, "3.12.2-stable", Const.CERT_KEY_PEM, Const.CERT_CERT_PEM));
    }

    public ServerImageProperties getProperties(TlsImplementationType type) {
        for (ServerImageProperties properties : propertyList) {
            if (properties.getType() == type) {
                return properties;
            }
        }
        throw new PropertyNotFoundException("No server properties found for: " + type.name());
    }
}
