/*
 * TLS-Docker-Library - A collection of open source TLS clients and servers
 *
 * Copyright 2017-2024 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tls.subject.docker.build;

import de.rub.nds.tls.subject.TlsImplementationType;
import java.nio.file.Path;
import java.util.Map;

public class LibraryBuildInformation {
    private final TlsImplementationType implementationType;
    private final Map<String, Path> versionDockerfileMap;

    public LibraryBuildInformation(
            TlsImplementationType implementationType, Map<String, Path> versionDockerfileMap) {
        this.implementationType = implementationType;
        this.versionDockerfileMap = versionDockerfileMap;
    }

    public TlsImplementationType getImplementationType() {
        return implementationType;
    }

    public Map<String, Path> getVersionDockerfileMap() {
        return versionDockerfileMap;
    }

    public boolean versionKnown(String version) {
        return versionDockerfileMap.keySet().contains(version.toLowerCase());
    }
}
