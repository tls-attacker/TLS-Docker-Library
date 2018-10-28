/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rub.nds.tls.subject.params;

import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.TlsInstance;
import de.rub.nds.tls.subject.TlsImplementationType;
import de.rub.nds.tls.subject.docker.DockerTlsManagerFactory;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Test;

public class AvailableClientVersionsTest {
    
    private static final String HOSTNAME = "nds.tls-docker-library-test.de";
    private static final String IP = "172.17.0.1";
    private static final int PORT = 8000;
    private static final int CONNECTION_TIMEOUT = 10;
    
    public AvailableClientVersionsTest() {
    }
    
    @Test
    public void listAllClients() {
        DockerTlsManagerFactory factory = new DockerTlsManagerFactory();
        System.out.println("Available Clients: ");
        for (TlsImplementationType type : TlsImplementationType.values()) {
            List<String> availableVersions = factory.getAvailableVersions(ConnectionRole.CLIENT, type);
            System.out.println("Client version: " + type);
            for (String version : availableVersions) {
                System.out.println(version);
            }
        }
    }
    
    @Test
    public void testAllVersionsFunctional() {
        Configurator.setRootLevel(org.apache.logging.log4j.Level.OFF);
        DockerTlsManagerFactory factory = new DockerTlsManagerFactory();
        System.out.println("Functional Clients: ");
        TlsTestServer testServer = new TlsTestServer(PORT);
        testServer.start();
        for (TlsImplementationType type : TlsImplementationType.values()) {
            List<String> availableVersions = factory.getAvailableVersions(ConnectionRole.CLIENT, type);
            for (String version : availableVersions) {
                try {
                    System.out.println(type.name() + ":" + version + " - " + isFunctional(testServer, factory, type, version));
                } catch (Exception E) {
                    E.printStackTrace();
                    System.out.println(type.name() + ":" + version + "       ERROR");
                }
            }
        }
        try {
            testServer.stop(IP, PORT);
        } catch (IOException ex) {
            Logger.getLogger(AvailableClientVersionsTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean isFunctional(TlsTestServer testServer, DockerTlsManagerFactory factory, TlsImplementationType type, String version) {
        TlsInstance client = null;
        testServer.setIsConnectionSuccessful(false);
        try {
            if (version == null || factory == null || type == null) {
                System.out.println("Null: " + version);
                return false;
            }
            try {
                client = factory.getClient(type, version, IP, HOSTNAME, PORT);
                boolean waiting = true;
                int timeout = 0;
                while (waiting && timeout<CONNECTION_TIMEOUT) {
                    if (testServer.isConnectionSuccessful()) {
                        waiting=false;
                    }
                    TimeUnit.SECONDS.sleep(1);
                    timeout++;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
            return testServer.isConnectionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            if (client != null) {
                client.kill();
            }
        }
    }
}
