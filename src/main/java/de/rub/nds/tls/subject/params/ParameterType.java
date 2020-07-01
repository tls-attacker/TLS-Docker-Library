package de.rub.nds.tls.subject.params;

public enum ParameterType {
    NONE, //Undefined Parameterfunction 
    HOST_PORT, //To who we shall connect to / where we should open our port
    VERIFY_DEPTH, //How deep certificates should be validated
    CERTIFICATE_KEY, //The cert and key which should be used_//PEM
    JKS_CERTIFICATE_KEY, //The cert and key which should be used_//JKS
    LOOP, //Keeps the Server from closing after a single connection
    NO_CLIENT_AUTHENTICATION, //Some servers/clients have client authentication active by default
    CA_CERTIFICATE, //The ca cert which should be used_//CRT
    INSECURE // Disables server certificate validation
}
