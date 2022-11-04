/*
 * TLS-Docker-Library - A collection of open source TLS clients and servers
 *
 * Copyright 2017-2022 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tls.subject.params;

import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.TlsImplementationType;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * @author robert
 */
public class ParameterProfileSerializerTest {

    public ParameterProfileSerializerTest() {}

    @Test
    public void testWrite() throws Exception {
        List<String> versionList = new LinkedList<>();
        versionList.add("1.1.0f");
        versionList.add("1.1.0g");
        List<Parameter> parameterList = new LinkedList<>();
        parameterList.add(new Parameter("-port [port]", ParameterType.HOST_PORT));
        parameterList.add(new Parameter("-cert [cert] -key [key]", ParameterType.CERTIFICATE_KEY));
        ParameterProfile profile =
                new ParameterProfile(
                        "openssl_default",
                        "Default Profile for Openssl",
                        TlsImplementationType.OPENSSL,
                        ConnectionRole.SERVER,
                        versionList,
                        parameterList);
        System.out.println(ParameterProfileSerializer.write(profile));
    }
}
