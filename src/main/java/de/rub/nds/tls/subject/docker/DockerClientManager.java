/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tls.subject.docker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DockerClientManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static DockerClient DOCKER = null;
    private static DockerClientConfig DCONFIG = null;
    private static DockerHttpClient DHTTPCLIENT = null;

    public static DockerClient getDockerClient() {
        if (DOCKER == null) {
            DOCKER = getNewDockerClient();
        }
        return DOCKER;
    }

    private static void ensureConfigExists() {
        if (DCONFIG == null) {
            DefaultDockerClientConfig.Builder cfgBuilder = DefaultDockerClientConfig.createDefaultConfigBuilder();
            if (System.getenv("DOCKER_HOST") == null && System.getProperty("os.name").startsWith("Windows")) {
                cfgBuilder = cfgBuilder.withDockerHost("npipe:////./pipe/docker_engine");
            }
            DCONFIG = cfgBuilder.build();
        }
    }

    public static DockerClient getNewDockerClient() {
        ensureConfigExists();
        if (DHTTPCLIENT == null) {
            DHTTPCLIENT = new ApacheDockerHttpClient.Builder().dockerHost(DCONFIG.getDockerHost())
                .sslConfig(DCONFIG.getSSLConfig()).build();
        }
        return DockerClientImpl.getInstance(DCONFIG, DHTTPCLIENT);
    }

    private DockerClientManager() {
        throw new IllegalStateException("Utility class");
    }
}