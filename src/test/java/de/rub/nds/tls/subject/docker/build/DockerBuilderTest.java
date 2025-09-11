/*
 * TLS-Docker-Library - A collection of open source TLS clients and servers
 *
 * Copyright 2017-2024 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tls.subject.docker.build;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.TlsImplementationType;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class DockerBuilderTest {

    @Test
    public void testReadLibraryDirectories() {
        Map<TlsImplementationType, Path> directories = DockerBuilder.readLibraryDirectories();
        DockerBuilder.getBuildInformationMap(directories);
        DockerBuilder testBuilder = new DockerBuilder();
        DockerfileArguments dockerfileArguments =
                testBuilder
                        .getKnownBuildableLibraries()
                        .get(TlsImplementationType.OPENSSL)
                        .getDockerfileArgumentsForVersion("1.1.1i");
        assertEquals("Dockerfile-1_1_1x", dockerfileArguments.getDockerfileName());
    }

    @Test
    public void testGetBuildFlagParameterTagHandlesEncoding() {
        // Test that getBuildFlagParameterTag handles UTF-8 encoding correctly
        String tag =
                DockerBuilder.getBuildFlagParameterTag(
                        TlsImplementationType.OPENSSL,
                        "1.1.1",
                        ConnectionRole.SERVER,
                        "test-flag-with-special-chars-ü-ö-ä");
        assertNotNull(tag);
        // The tag should be deterministic for the same input
        String tag2 =
                DockerBuilder.getBuildFlagParameterTag(
                        TlsImplementationType.OPENSSL,
                        "1.1.1",
                        ConnectionRole.SERVER,
                        "test-flag-with-special-chars-ü-ö-ä");
        assertEquals(tag, tag2);
    }
}
