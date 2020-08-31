package de.rub.nds.tls.subject.docker;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ExecCreation;

import de.rub.nds.tls.subject.instance.ExecInstance;

public class DockerExecInstance implements ExecInstance {
    private final DockerClient DOCKER;
    protected final ExecCreation execCreation;
    protected final LogStream logStream;

    public DockerExecInstance(ExecCreation execCreation) throws DockerException, InterruptedException {
        // if we are not using detach in execStart we must use our own docker client (as
        // we otherwise block other execStarts)
        DOCKER = DockerClientManager.getNewDockerClient();
        this.execCreation = execCreation;
        logStream = DOCKER.execStart(execCreation.id());
    }

    @Override
    public void close() {
        // closing the logstream caused issues...
        DOCKER.close();
    }

    @Override
    public boolean isRunning() throws DockerException, InterruptedException {
        return DOCKER.execInspect(execCreation.id()).running();
    }
}