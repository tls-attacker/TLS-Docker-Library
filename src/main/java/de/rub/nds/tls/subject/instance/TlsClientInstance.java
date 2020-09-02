package de.rub.nds.tls.subject.instance;

public interface TlsClientInstance extends TlsInstance {
    ExecInstance connect();

    ExecInstance connect(String host, int targetPort);

    ExecInstance connect(String host, int targetPort, String additionalParameters, Boolean parallelize, Boolean insecureConnection);
}