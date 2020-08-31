package de.rub.nds.tls.subject.instance;

import com.spotify.docker.client.exceptions.DockerException;

public interface TlsClientInstance extends TlsInstance {
    ExecInstance connect() throws DockerException, InterruptedException;

    ExecInstance connect(String host, int targetPort) throws DockerException, InterruptedException;

    ExecInstance connect(String host, int targetPort, String additionalParameters, Boolean parallelize, Boolean insecureConnection) throws DockerException, InterruptedException;
}