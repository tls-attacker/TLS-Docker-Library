package de.rub.nds.tls.subject.params;

import static org.junit.Assert.assertEquals;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;

import org.junit.Test;

import de.rub.nds.tls.subject.docker.DockerClientManager;

public class DockerWorkingTest {
    @Test
    public void isDockerOK() throws DockerException, InterruptedException {
        DockerClient docker = DockerClientManager.getDockerClient();
        assertEquals("OK", docker.ping());
    }
}