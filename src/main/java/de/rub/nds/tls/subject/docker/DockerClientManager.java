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
            DOCKER = getNewDockerClient();
        }
        return DOCKER;
    }

    public static DockerClient getNewDockerClient() {
        try {
            return DefaultDockerClient.fromEnv().build();
        } catch (DockerCertificateException e) {
            LOGGER.warn("Could not build docker client from env; Falling back to unix socket", e);
            return new DefaultDockerClient("unix:///var/run/docker.sock");
        }
    }

    private DockerClientManager() {
        throw new IllegalStateException("Utility class");
    }
}