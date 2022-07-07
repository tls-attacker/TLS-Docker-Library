/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tls.subject.params;

import static org.junit.Assert.assertTrue;

import com.github.dockerjava.api.DockerClient;

import org.junit.Test;

import de.rub.nds.tls.subject.docker.DockerClientManager;

public class DockerWorkingTest {
    @Test
    public void isDockerOK() {
        DockerClient docker = DockerClientManager.getDockerClient();
        docker.pingCmd().exec();
        assertTrue(true); // assert that no exception occured
    }
}