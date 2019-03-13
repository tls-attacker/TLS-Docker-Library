package de.rub.nds.tls.subject.params;

import de.rub.nds.tls.subject.TlsImplementationType;
import de.rub.nds.tls.subject.TlsServer;
import de.rub.nds.tls.subject.docker.DockerTlsManagerFactory;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Test;

public class AvailableServerVersionsTest {

    public AvailableServerVersionsTest() {
    }

    @Test
    public void listAllServers() {
        DockerTlsManagerFactory factory = new DockerTlsManagerFactory();
        for (TlsImplementationType type : TlsImplementationType.values()) {
            List<String> availableVersions = factory.getAvailableServerVersions(type);
            System.out.println("Server version: " + type);
            for (String version : availableVersions) {
                System.out.println(version);
            }
        }
    }

    @Test
    public void testAllVersionsFunctional() {
        Configurator.setRootLevel(org.apache.logging.log4j.Level.OFF);
        DockerTlsManagerFactory factory = new DockerTlsManagerFactory();
        for (TlsImplementationType type : TlsImplementationType.values()) {
            List<String> availableVersions = factory.getAvailableServerVersions(type);
            for (String version : availableVersions) {
                try {
                    System.out.println(type.name() + ":" + version + " - " + isFunctional(factory, type, version));
                } catch (Exception E) {
                    E.printStackTrace();
                    System.out.println(type.name() + ":" + version + "       ERROR");
                }
            }
        }
    }

    public boolean isFunctional(DockerTlsManagerFactory factory, TlsImplementationType type, String version) {
        TlsServer server = null;
        try {
            if (version == null || factory == null || type == null) {
                System.out.println("Null: " + version);
                return false;
            }
            try {
                server = factory.getServer(type, version);
            } catch (Exception E) {
                E.printStackTrace();
                return false;
            }
            Socket socket = new Socket(server.getHost(), server.getPort());
            if (socket.isConnected()) {
                return true;
            } else {
                return false;
            }
        } catch (IOException ex) {
            return false;
        } finally {
            if (server != null) {
                server.kill();
            }
        }

    }

    @Test
    public void temptestAllVersionsFunctional() {
        Configurator.setRootLevel(org.apache.logging.log4j.Level.OFF);
        DockerTlsManagerFactory factory = new DockerTlsManagerFactory();
        List<String> availableVersions = factory.getAvailableServerVersions(TlsImplementationType.JSSE);
        for (String version : availableVersions) {
            try {
                System.out.println(TlsImplementationType.JSSE.name() + ":" + version + " - " + isFunctional(factory, TlsImplementationType.JSSE, version));
            } catch (Exception E) {
                E.printStackTrace();
                System.out.println(TlsImplementationType.JSSE.name() + ":" + version + "       ERROR");
            }
        }
    }
}
