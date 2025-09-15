/*
 * TLS-Docker-Library - A collection of open source TLS clients and servers
 *
 * Copyright 2017-2022 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tls.subject.docker;

import static org.junit.jupiter.api.Assertions.*;

import de.rub.nds.tls.subject.TlsImplementationType;
import de.rub.nds.tls.subject.params.Parameter;
import de.rub.nds.tls.subject.params.ParameterProfile;
import de.rub.nds.tls.subject.params.ParameterType;
import de.rub.nds.tls.subject.properties.ImageProperties;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class DockerTlsManagerFactoryTest {

    private static final String TEST_EC_CERT = "/cert/ec256cert.pem";
    private static final String TEST_EC_KEY = "/cert/ec256key.pem";
    private static final String TEST_EC_COMBINED = "/cert/ec256combined.pem";

    @Test
    public void testClientBuilderWithCustomCertificatePaths() {
        DockerTlsManagerFactory.TlsClientInstanceBuilder builder =
                DockerTlsManagerFactory.getTlsClientBuilder(
                        TlsImplementationType.OPENSSL, "1.1.1i");

        // Configure custom certificate paths
        builder.certificatePath(TEST_EC_CERT).keyPath(TEST_EC_KEY).combinedPath(TEST_EC_COMBINED);

        // Verify the builder returns itself for method chaining
        assertSame(builder, builder.certificatePath(TEST_EC_CERT));
        assertSame(builder, builder.keyPath(TEST_EC_KEY));
        assertSame(builder, builder.combinedPath(TEST_EC_COMBINED));
    }

    @Test
    public void testServerBuilderWithCustomCertificatePaths() {
        DockerTlsManagerFactory.TlsServerInstanceBuilder builder =
                DockerTlsManagerFactory.getTlsServerBuilder(
                        TlsImplementationType.OPENSSL, "1.1.1i");

        // Configure custom certificate paths
        builder.certificatePath(TEST_EC_CERT).keyPath(TEST_EC_KEY).combinedPath(TEST_EC_COMBINED);

        // Verify the builder returns itself for method chaining
        assertSame(builder, builder.certificatePath(TEST_EC_CERT));
        assertSame(builder, builder.keyPath(TEST_EC_KEY));
        assertSame(builder, builder.combinedPath(TEST_EC_COMBINED));
    }

    @Test
    public void testEffectiveImagePropertiesWithCustomPaths() {
        // Create a mock DockerTlsInstance to test getEffectiveImageProperties
        ImageProperties originalProps =
                new ImageProperties(
                        de.rub.nds.tls.subject.ConnectionRole.SERVER,
                        TlsImplementationType.OPENSSL,
                        "1.1.1i",
                        4433,
                        "/cert/rsa2048key.pem",
                        "/cert/rsa2048cert.pem",
                        "/cert/rsa2048combined.pem");

        // Create a test instance with custom paths
        TestDockerTlsInstance instance =
                new TestDockerTlsInstance(
                        originalProps, TEST_EC_CERT, TEST_EC_KEY, TEST_EC_COMBINED);

        ImageProperties effectiveProps = instance.getEffectiveImageProperties();

        // Verify custom paths are used
        assertEquals(TEST_EC_CERT, effectiveProps.getDefaultCertPath());
        assertEquals(TEST_EC_KEY, effectiveProps.getDefaultKeyPath());
        assertEquals(TEST_EC_COMBINED, effectiveProps.getDefaultCertKeyCombinedPath());

        // Verify other properties are preserved
        assertEquals(originalProps.getRole(), effectiveProps.getRole());
        assertEquals(originalProps.getType(), effectiveProps.getType());
        assertEquals(originalProps.getDefaultVersion(), effectiveProps.getDefaultVersion());
        assertEquals(originalProps.getInternalPort(), effectiveProps.getInternalPort());
    }

    @Test
    public void testEffectiveImagePropertiesWithoutCustomPaths() {
        ImageProperties originalProps =
                new ImageProperties(
                        de.rub.nds.tls.subject.ConnectionRole.SERVER,
                        TlsImplementationType.OPENSSL,
                        "1.1.1i",
                        4433,
                        "/cert/rsa2048key.pem",
                        "/cert/rsa2048cert.pem",
                        "/cert/rsa2048combined.pem");

        // Create a test instance without custom paths
        TestDockerTlsInstance instance = new TestDockerTlsInstance(originalProps, null, null, null);

        ImageProperties effectiveProps = instance.getEffectiveImageProperties();

        // Verify original properties are returned unchanged
        assertSame(originalProps, effectiveProps);
    }

    @Test
    public void testEffectiveImagePropertiesWithPartialCustomPaths() {
        ImageProperties originalProps =
                new ImageProperties(
                        de.rub.nds.tls.subject.ConnectionRole.SERVER,
                        TlsImplementationType.OPENSSL,
                        "1.1.1i",
                        4433,
                        "/cert/rsa2048key.pem",
                        "/cert/rsa2048cert.pem",
                        "/cert/rsa2048combined.pem");

        // Create a test instance with only custom cert path
        TestDockerTlsInstance instance =
                new TestDockerTlsInstance(originalProps, TEST_EC_CERT, null, null);

        ImageProperties effectiveProps = instance.getEffectiveImageProperties();

        // Verify only custom cert path is used, others remain original
        assertEquals(TEST_EC_CERT, effectiveProps.getDefaultCertPath());
        assertEquals(originalProps.getDefaultKeyPath(), effectiveProps.getDefaultKeyPath());
        assertEquals(
                originalProps.getDefaultCertKeyCombinedPath(),
                effectiveProps.getDefaultCertKeyCombinedPath());
    }

    // Test helper class to access protected methods
    private static class TestDockerTlsInstance extends DockerTlsInstance {
        public TestDockerTlsInstance(
                ImageProperties imageProperties,
                String customCertificatePath,
                String customKeyPath,
                String customCombinedPath) {
            // Create a minimal ParameterProfile for testing
            super(
                    null,
                    "test-container",
                    new ParameterProfile(
                            "test_profile",
                            "Test Profile",
                            TlsImplementationType.OPENSSL,
                            de.rub.nds.tls.subject.ConnectionRole.SERVER,
                            Arrays.asList("1.1.1i"),
                            Arrays.asList(
                                    new Parameter("-port [port]", ParameterType.HOST_PORT),
                                    new Parameter(
                                            "-cert [cert] -key [key]",
                                            ParameterType.CERTIFICATE_KEY))),
                    imageProperties,
                    "1.1.1i",
                    "",
                    de.rub.nds.tls.subject.ConnectionRole.SERVER,
                    true,
                    null,
                    null,
                    null,
                    customCertificatePath,
                    customKeyPath,
                    customCombinedPath);
        }

        @Override
        public ImageProperties getEffectiveImageProperties() {
            return super.getEffectiveImageProperties();
        }
    }
}
