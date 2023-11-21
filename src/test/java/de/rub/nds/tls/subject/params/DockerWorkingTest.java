/*
 * TLS-Docker-Library - A collection of open source TLS clients and servers
 *
 * Copyright 2017-2022 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tls.subject.params;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.dockerjava.api.DockerClient;
import de.rub.nds.tls.subject.docker.DockerClientManager;
import org.junit.jupiter.api.Test;

public class DockerWorkingTest {
    @Test
    public void isDockerOK() {
        DockerClient docker = DockerClientManager.getDockerClient();
        docker.pingCmd().exec();
        assertTrue(true); // assert that no exception occured
    }
}
