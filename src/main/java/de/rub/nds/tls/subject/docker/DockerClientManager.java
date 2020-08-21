package de.rub.nds.tls.subject.docker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DockerClientManager {
    private static DockerClient DOCKER = null;
    private static final Logger LOGGER = LogManager.getLogger(DockerTlsManagerFactory.class);

    public static DockerClient getDockerClient() {
        if (DOCKER == null) {
            try {
                DOCKER = DefaultDockerClient.fromEnv().build();
            } catch (DockerCertificateException e) {
                LOGGER.warn("Could not build docker client from env; Falling back to unix socket", e);
                DOCKER = new DefaultDockerClient("unix:///var/run/docker.sock");
            }
        }
        return DOCKER;
    }
}