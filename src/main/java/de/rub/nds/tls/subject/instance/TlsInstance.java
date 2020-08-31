package de.rub.nds.tls.subject.instance;

import com.spotify.docker.client.exceptions.DockerException;

import de.rub.nds.tls.subject.ConnectionRole;

public interface TlsInstance {
    void start() throws DockerException, InterruptedException;

    void stop() throws DockerException, InterruptedException;

    void stop(int secondsToWaitBeforeKilling) throws DockerException, InterruptedException;

    void kill() throws DockerException, InterruptedException;

    void restart() throws DockerException, InterruptedException;

    void remove() throws DockerException, InterruptedException;

    String getId();

    String getLogs() throws DockerException, InterruptedException;

    long getExitCode() throws DockerException, InterruptedException;

    @Override
    String toString();

    void close();

}