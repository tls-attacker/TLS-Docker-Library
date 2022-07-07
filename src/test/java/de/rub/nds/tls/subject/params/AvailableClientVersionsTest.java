/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.rub.nds.tls.subject.params;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Test;

import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.TlsImplementationType;
import de.rub.nds.tls.subject.docker.DockerExecInstance;
import de.rub.nds.tls.subject.docker.DockerTlsClientInstance;
import de.rub.nds.tls.subject.docker.DockerTlsManagerFactory;
import de.rub.nds.tls.subject.report.ContainerReport;
import de.rub.nds.tls.subject.report.InstanceContainer;

public class AvailableClientVersionsTest {

    private static final String HOSTNAME = "nds.tls-docker-library-test.de";
    // when running the tests on another os than linux you might need to change the
    // ip use `docker run --rm -it alpine-build:3.12 ping -c1 host.docker.internal`
    // to find the correct IP
    private static final String IP = "172.17.0.1";
    private static final int PORT = 8000;
    private static final int CONNECTION_TIMEOUT = 10;

    public AvailableClientVersionsTest() {
    }

    @Test
    public void listAllClients() {
        System.out.println("Available Clients: ");
        for (TlsImplementationType type : TlsImplementationType.values()) {
            List<String> availableVersions = DockerTlsManagerFactory.getAvailableVersions(ConnectionRole.CLIENT, type);
            System.out.println("Client version: " + type);
            for (String version : availableVersions) {
                System.out.println(version);
            }
        }
    }

    @Test
    public void testAllVersionsFunctional() throws JAXBException, IOException {
        Configurator.setRootLevel(org.apache.logging.log4j.Level.OFF);
        System.out.println("Functional Clients: ");
        TlsTestServer testServer = new TlsTestServer(PORT);
        testServer.start();
        ContainerReport report = new ContainerReport();
        for (TlsImplementationType type : TlsImplementationType.values()) {
            List<String> availableVersions = DockerTlsManagerFactory.getAvailableVersions(ConnectionRole.CLIENT, type);
            for (String version : availableVersions) {
                try {
                    boolean isFunctional = isFunctional(testServer, type, version);
                    System.out.println(type.name() + ":" + version + " - " + isFunctional);
                    report.addInstanceContainer(
                        new InstanceContainer(ConnectionRole.CLIENT, type, version, isFunctional));
                } catch (Exception E) {
                    E.printStackTrace();
                    System.out.println(type.name() + ":" + version + "       ERROR");
                }
            }
        }
        ContainerReport.write(new File("client_report.xml"), report);
        try {
            testServer.stop("localhost", PORT);
        } catch (IOException ex) {
            Logger.getLogger(AvailableClientVersionsTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isFunctional(TlsTestServer testServer, TlsImplementationType type, String version) {
        DockerTlsClientInstance client = null;
        DockerExecInstance ei = null;
        testServer.setIsConnectionSuccessful(false);
        try {
            if (version == null || type == null) {
                System.out.println("Null: " + version);
                return false;
            }
            client = DockerTlsManagerFactory.getTlsClientBuilder(type, version).ip(IP).hostname(HOSTNAME).port(PORT)
                .connectOnStartup(false).insecureConnection(false).build();
            client.start();
            ei = (DockerExecInstance) client.connect();
            boolean waiting = true;
            int timeout = 0;
            while (waiting && timeout < CONNECTION_TIMEOUT) {
                if (testServer.isConnectionSuccessful()) {
                    waiting = false;
                }
                TimeUnit.SECONDS.sleep(1);
                timeout++;
            }

            boolean res = testServer.isConnectionSuccessful();
            if (!res) {
                System.out.println("-Failed- Log:");
                for (String ln : ei.frameHandler.getLines()) {
                    System.out.println(ln);
                }
            }
            return res;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
