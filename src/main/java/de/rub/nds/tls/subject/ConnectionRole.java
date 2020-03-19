package de.rub.nds.tls.subject;

public enum ConnectionRole {
    CLIENT,
    SERVER;

    private static final String CLIENT_LABEL = "client_type";
    private static final String CLIENT_VERSION_LABEL = "client_version";
    private static final String SERVER_LABEL = "server_type";
    private static final String SERVER_VERSION_LABEL = "server_version";

    public static String getInstanceLabel(ConnectionRole role) {
        switch (role) {
            case CLIENT:
                return CLIENT_LABEL;
            case SERVER:
                return SERVER_LABEL;
            default:
                throw new IllegalArgumentException("Unknown ConnectionRole: " + role.name());
        }
    }

    public static String getInstanceVersionLabel(ConnectionRole role) {
        switch (role) {
            case CLIENT:
                return CLIENT_VERSION_LABEL;
            case SERVER:
                return SERVER_VERSION_LABEL;
            default:
                throw new IllegalArgumentException("Unknown ConnectionRole: " + role.name());
        }
    }
}
