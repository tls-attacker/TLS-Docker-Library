package de.rub.nds.tls.subject.docker;

public enum DockerTlsServerType {

    MBED("mbed", 4430, "debug_level=4"),
    OPENSSL("openssl", 4433, "-debug", "-state", "-msg", "-cert", Const.CERT_CERT_PEM, "-key", Const.CERT_KEY_PEM),
    DAMN_VULNERABLE_OPENSSL("damnvulnerableopenssl", 4433, "-cert", Const.CERT_CERT_PEM, "-key", Const.CERT_KEY_PEM),
    NSS("nss", 4430, "-p", "4430", "-v", "-d", "/cert/db/", "-n", "cert"),
    GNUTLS("gnutls", 5556, "--disable-client-cert", "--debug", "4", "--x509certfile", Const.CERT_CERT_PEM, "--x509keyfile", Const.CERT_KEY_PEM),
    BORINGSSL("boringssl", 4430, "-accept", "4430", "-debug", "-loop", "-cert", Const.CERT_CERT_PEM, "-key", Const.CERT_KEY_PEM),
    S2N("s2n", 4430, "0.0.0.0", "4430"),
    LIBRESSL("libressl", 4433, "-debug", "-cert", Const.RUST_TEST_CA_FULLCHAIN, "-key", Const.RUST_TEST_CA_KEY),
    BEARSSL("bearssl", 4433, "-trace", "-key", Const.CERT_KEY_PEM, "-cert", Const.CERT_CERT_PEM),
    BOTAN("botan", 443, Const.CERT_CERT_PEM, Const.CERT_KEY_PEM, "--policy=compat.txt"),
    WOLFSSL("wolfssl", 11111, "-c", Const.CERT_CERT_PEM, "-k", Const.CERT_KEY_PEM, "-d", "-b"),
    RUSTLS("rustls", 443, "--verbose", "--certs", Const.RUST_TEST_CA_FULLCHAIN, "--key", Const.RUST_TEST_CA_KEY, "echo"),
    BOUNCYCASTLE("bouncycastle", 4433, "4433", "/cert/keys.jks", "password", "rsa2048", "/cert/keys.jks", "password", "ec256");

    private final String name;
    private final int internalPort;
    private final String[] params;

    DockerTlsServerType(String name, int internalPort, String... params) {
        this.name = name;
        this.internalPort = internalPort;
        this.params = params;
    }

    public int getInternalPort() {
        return internalPort;
    }

    public String getName() {
        return name;
    }

    public String[] getParams() {
        return params;
    }

    private static class Const {

        static final String CERT_KEY_PEM = "/cert/rsa2048key.pem";
        static final String CERT_CERT_PEM = "/cert/rsa2048cert.pem";
        static final String RUST_TEST_CA_KEY = "/cert/test-ca/rsa/end.rsa";
        static final String RUST_TEST_CA_FULLCHAIN = "/cert/test-ca/rsa/end.fullchain";
    }

}
