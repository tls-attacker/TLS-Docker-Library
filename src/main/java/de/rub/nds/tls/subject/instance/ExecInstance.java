package de.rub.nds.tls.subject.instance;

import com.spotify.docker.client.exceptions.DockerException;

public interface ExecInstance {
    boolean isRunning() throws DockerException, InterruptedException;

    void close();
}