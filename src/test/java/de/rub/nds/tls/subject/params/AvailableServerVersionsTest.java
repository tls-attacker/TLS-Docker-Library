package de.rub.nds.tls.subject.params;

import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.TlsImplementationType;
import de.rub.nds.tls.subject.TlsInstance;
import de.rub.nds.tls.subject.docker.DockerTlsManagerFactory;
import de.rub.nds.tls.subject.report.ContainerReport;
import de.rub.nds.tls.subject.report.InstanceContainer;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Test;

public class AvailableServerVersionsTest {

    private Logger LOGGER = LogManager.getLogger();

    public AvailableServerVersionsTest() {
    }

    @Test
    public void listAllServers() {
        DockerTlsManagerFactory factory = new DockerTlsManagerFactory();
        for (TlsImplementationType type : TlsImplementationType.values()) {
            List<String> availableVersions = factory.getAvailableVersions(ConnectionRole.SERVER, type);
            System.out.println("Server version: " + type);
            for (String version : availableVersions) {
                System.out.println(version);
            }
        }
    }

    @Test
    public void testAllVersionsFunctional() throws JAXBException, IOException {
        Configurator.setRootLevel(org.apache.logging.log4j.Level.OFF);
        DockerTlsManagerFactory factory = new DockerTlsManagerFactory();
        ContainerReport report = new ContainerReport();
        for (TlsImplementationType type : TlsImplementationType.values()) {
            List<String> availableVersions = factory.getAvailableVersions(ConnectionRole.SERVER, type);
            for (String version : availableVersions) {
                try {
                    boolean isFunctional = isFunctional(factory, type, version);
                    System.out.println(type.name() + ":" + version + " - " + isFunctional);
                    report.addInstanceContainer(new InstanceContainer(ConnectionRole.SERVER, type, version, isFunctional));
                } catch (Exception E) {
                    E.printStackTrace();
                    System.out.println(type.name() + ":" + version + "       ERROR");
                }
                
            }
        }
        ContainerReport.write(new File("server_report.xml"), report);
    }

    public boolean isFunctional(DockerTlsManagerFactory factory, TlsImplementationType type, String version) {
        TlsInstance server = null;
        try {
            if (version == null || factory == null || type == null) {
                System.out.println("Null: " + version);
                return false;
            }
            try {
                server = factory.getServer(type, version);
                server.start();
            } catch (Exception E) {
                LOGGER.warn("Instance seems not runnable", E);
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
}
