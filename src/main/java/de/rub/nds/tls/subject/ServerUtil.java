/*
 * TLS-Docker-Library - A collection of open source TLS clients and servers
 *
 * Copyright 2017-2022 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tls.subject;

import de.rub.nds.tls.subject.exceptions.ImplementationDidNotStartException;
import java.io.IOException;
import java.net.Socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerUtil {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final int SERVER_POLL_INTERVAL_MILLISECONDS = 50;

    private static final int TIMEOUT_WAIT_FOR_SERVER_SPINUP_MILLISECONDS = 10000;

    public void waitUntilServerIsOnline(String host, int port) {
        long startTime = System.currentTimeMillis();
        while (!isServerOnline(host, port)) {
            if (startTime + TIMEOUT_WAIT_FOR_SERVER_SPINUP_MILLISECONDS
                    < System.currentTimeMillis()) {
                throw new ImplementationDidNotStartException("Could not start Server: Timeout");
            }
            try {
                Thread.sleep(SERVER_POLL_INTERVAL_MILLISECONDS);
            } catch (InterruptedException ex) {
                throw new ImplementationDidNotStartException(
                        "Interrupted while waiting for Server", ex);
            }
        }
    }

    public boolean isServerOnline(String address, int port) {
        try {
            Socket ss = new Socket(address, port);
            if (ss.isConnected()) {
                ss.close();
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            LOGGER.debug("Server is not online yet", e);
            return false;
        }
    }
}
