package de.rub.nds.tls.subject.system;

/**
 * Creates TLS-Server Instances from the Command Line
 * Holds the Config for each TLS-Server
 */

public class SystemTlsServerManagerFactory {

    private SystemTlsServerManagerFactory() {}

    public static SystemTlsServerManager get(SystemTlsServerType serverType) {
        return new SystemTlsServerManager()
                .setName(serverType.name)
                .setCommand(serverType.command);
    }

    public enum SystemTlsServerType {
        OPENSSL("openssl", "openssl", "s_server", "-debug", "-key", Const.PREFIX + Const.CERT_KEY_PEM, "-cert", Const.PREFIX + Const.CERT_CERT_PEM, "-port", "<port>"),

        LIBRESSL("libressl", System.getenv("HOME") + "/.bin/libressl/openssl", "s_server", "-debug", "-key", Const.PREFIX + Const.CERT_KEY_PEM, "-cert", Const.PREFIX + Const.CERT_CERT_PEM, "-port", "<port>");

        private final String name;
        private final String[] command;

        SystemTlsServerType(String name, String... command) {
            this.name = name;
            this.command = command;
        }

        private static class Const {

            static final String PREFIX = System.getenv("HOME") + "/docker/tls/certs/";
            static final String CERT_KEY_PEM = "key.pem";
            static final String CERT_CERT_PEM = "cert.pem";
            static final String RUST_TEST_CA_KEY = "/cert/test-ca/rsa/end.rsa";
            static final String RUST_TEST_CA_FULLCHAIN = "/cert/test-ca/rsa/end.fullchain";
        }

    }

}
