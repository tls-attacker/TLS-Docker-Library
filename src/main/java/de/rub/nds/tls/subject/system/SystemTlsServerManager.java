package de.rub.nds.tls.subject.system;

import de.rub.nds.tls.subject.TlsServer;
import de.rub.nds.tls.subject.TlsServerManager;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Mange a TLS-Server via Command Line
 *
 * One instance is needed for each server type.
 */

public class SystemTlsServerManager implements TlsServerManager {

    private String[] command;
    private ConcurrentMap<String, Process> processes;
    private String name;

    SystemTlsServerManager() {
        processes = new ConcurrentHashMap<>();
    }

    @Override
    public TlsServer getTlsServer() {
        int port = 0;
        String id = RandomStringUtils.randomAlphanumeric(16);
        try {
            //get random free port
            ServerSocket serverSocket = new ServerSocket(0);
            port = serverSocket.getLocalPort();
            serverSocket.close();

            //System.out.println(port);

            List<String> commandList = Arrays.asList(command.clone());
            Collections.replaceAll(commandList, "<port>", port + "");

            //System.out.println(commandList);

            Process process = new ProcessBuilder()
                    .command(commandList)
                    .redirectErrorStream(true)
                    .start();

            processes.put(id, process);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new TlsServer(id, port, name, this);
    }

    @Override
    public void killTlsServer(TlsServer tlsServer) {
        Process process = processes.get(tlsServer.id);
        if(process != null) {
            process.destroy();
            try {
                tlsServer.exitCode = process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getLogsFromTlsServer(TlsServer tlsServer) {
        Process process = processes.get(tlsServer.id);
        if(process != null && !process.isAlive()) {
            try {
                return IOUtils.toString(process.getInputStream(), Charset.defaultCharset());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "-";
    }

    @Override
    public String getServerName(TlsServer tlsServer) {
        return name;
    }

    public SystemTlsServerManager setCommand(String[] command) {
        this.command = command;
        return this;
    }

    public SystemTlsServerManager setName(String name) {
        this.name = name;
        return this;
    }
}
