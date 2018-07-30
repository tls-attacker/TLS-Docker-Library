/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rub.nds.tls.subject.params;

import de.rub.nds.tls.subject.TlsClient;
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
    
    private static final String HOST = "172.17.0.1";
    private static final int PORT = 8000;
    
    public AvailableClientVersionsTest() {
    }
    
    @Test
    public void listAllClients() {
        DockerTlsManagerFactory factory = new DockerTlsManagerFactory();
        System.out.println("Available Clients: ");
        for (TlsImplementationType type : TlsImplementationType.values()) {
            List<String> availableVersions = factory.getAvailableClientVersions(type);
            System.out.println("Client version: " + type);
            for (String version : availableVersions) {
                System.out.println(version);
            }
        }
    }
    
    @Test
    public void testAllVersionsFunctional() throws IOException {
        Configurator.setRootLevel(org.apache.logging.log4j.Level.OFF);
        DockerTlsManagerFactory factory = new DockerTlsManagerFactory();
        System.out.println("Functional Clients: ");
        
        for (TlsImplementationType type : TlsImplementationType.values()) {
            List<String> availableVersions = factory.getAvailableClientVersions(type);
            for (String version : availableVersions) {
                try {
                    System.out.println(type.name() + ":" + version + " - " + isFunctional(factory, type, version));
                } catch (Exception E) {
                    E.printStackTrace();
                    System.out.println(type.name() + ":" + version + "       ERROR");
                }
            }
        }
        System.out.println("Testserver stopped");
    }
    
    public boolean isFunctional(DockerTlsManagerFactory factory, TlsImplementationType type, String version) throws IOException {
        TlsClient client = null;
        TlsTestServer testServer = new TlsTestServer(PORT);
        testServer.start();
        testServer.setIsConnectionSuccessful(false);
        try {
            if (version == null || factory == null || type == null) {
                System.out.println("Null: " + version);
                return false;
            }
            try {
                client = factory.getClient(type, version, HOST, PORT);
                //System.out.println("Client started successfully");
                TimeUnit.SECONDS.sleep(3); //Necessary time to wait for connection = 3 seconds
            } catch (Exception ex) {
                ex.printStackTrace();
                //System.err.println("Failed to start client");
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
            testServer.stop(HOST, PORT);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(AvailableClientVersionsTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
//    public boolean isFunctionalTmp(DockerTlsManagerFactory factory, TlsImplementationType type, String version) throws IOException {
//        TlsClient client = null;
//        TlsTestServer testServer = new TlsTestServer(PORT);
//        testServer.start();
//        testServer.setIsConnectionSuccessful(false);
//        try {
//            if (version == null || factory == null || type == null) {
//                System.out.println("Null: " + version);
//                return false;
//            }
//            try {
//                //BearSSLClient
//                //client = factory.getClient(type, version, HOST, PORT);
//                //OpenSSLCommand
//                String command = "openssl s_client -connect "+HOST+":"+PORT;
//                Process proc = Runtime.getRuntime().exec(command);
//                System.out.println("Client started successfully");
//                TimeUnit.SECONDS.sleep(1);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                System.err.println("Failed to start client");
//                return false;
//            }
//            return testServer.isConnectionSuccessful();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return false;
//        } finally {
//            if (client != null) {
//                client.kill();
//            }
//            testServer.stop(HOST, PORT);
//        }
//    }
//    
//    @Test
//    public void testAllVersionsFunctionalTmp() throws IOException {
//        Configurator.setRootLevel(org.apache.logging.log4j.Level.OFF);
//        DockerTlsManagerFactory factory = new DockerTlsManagerFactory();
//        List<String> availableVersions = factory.getAvailableClientVersions(TlsImplementationType.BEARSSL);
//        for (String version : availableVersions) {
//            try {
//                //System.out.println(TlsImplementationType.BEARSSL + ":" + version + " - " + isFunctional(factory, TlsImplementationType.BEARSSL, version));
//                System.out.println("OpenSSLCommandTest: " + isFunctionalTmp(factory, TlsImplementationType.BEARSSL, version));
//            } catch (Exception E) {
//                E.printStackTrace();
//                //System.out.println(TlsImplementationType.BEARSSL + ":" + version + "       ERROR");
//                System.out.println("OpenSSLCommandTest:       ERROR");
//            }
//        }
//        System.out.println("Testserver stopped");
//    }
}
