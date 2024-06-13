/*
 * TLS-Docker-Library - A collection of open source TLS clients and servers
 *
 * Copyright 2017-2024 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tls.subject.docker.build;

public class DockerfileArguments {
    private final String dockerfileName;
    private final String versionBuildArgument;

    public DockerfileArguments(String dockerfileName, String versionBuildArgument) {
        this.dockerfileName = dockerfileName;
        this.versionBuildArgument = versionBuildArgument;
    }

    public String getVersionBuildArgument() {
        return versionBuildArgument;
    }

    public String getDockerfileName() {
        return dockerfileName;
    }
}
