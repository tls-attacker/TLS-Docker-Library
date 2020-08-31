package de.rub.nds.tls.subject.docker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
            DOCKER = (DockerClient) Proxy.newProxyInstance(DockerClient.class.getClassLoader(), new Class[] { DockerClient.class }, new ThreadLocalDockerClient());
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

    private static class ThreadLocalDockerClient implements InvocationHandler {
        @SuppressWarnings("squid:S5164") // sonarlint: "ThreadLocal" variables should be cleaned up when no longer used
        // This might be a bit of a memory leak, but we have no idea when a thread does
        // not need the docker client anymore, thus we cannot call remove :S
        private static ThreadLocal<DockerClient> LDOCKER = new ThreadLocal<>();

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (LDOCKER.get() == null) {
                LDOCKER.set(getNewDockerClient());
            }
            DockerClient docker = LDOCKER.get();
            return method.invoke(docker, args);
        }

    }
}