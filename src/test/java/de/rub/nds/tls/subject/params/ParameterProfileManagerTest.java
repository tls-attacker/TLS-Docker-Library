/*
 * TLS-Docker-Library - A collection of open source TLS clients and servers
 *
 * Copyright 2017-2022 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tls.subject.params;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.Test;

/**
 * @author robert
 */
public class ParameterProfileManagerTest {

    public ParameterProfileManagerTest() {}

    @Test
    public void testGetDefaultProfile() {
        Configurator.setRootLevel(Level.DEBUG);
        ParameterProfileManager manager = new ParameterProfileManager();
    }
}
