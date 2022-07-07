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

import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.TlsImplementationType;
import java.util.LinkedList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author robert
 */
public class ParameterProfileSerializerTest {

    public ParameterProfileSerializerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testWrite() throws Exception {
        List<String> versionList = new LinkedList<>();
        versionList.add("1.1.0f");
        versionList.add("1.1.0g");
        List<Parameter> parameterList = new LinkedList<>();
        parameterList.add(new Parameter("-port [port]", ParameterType.HOST_PORT));
        parameterList.add(new Parameter("-cert [cert] -key [key]", ParameterType.CERTIFICATE_KEY));
        ParameterProfile profile = new ParameterProfile("openssl_default", "Default Profile for Openssl",
            TlsImplementationType.OPENSSL, ConnectionRole.SERVER, versionList, parameterList);
        System.out.println(ParameterProfileSerializer.write(profile));
    }
}
