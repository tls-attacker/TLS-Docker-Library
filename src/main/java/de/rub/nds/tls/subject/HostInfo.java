package de.rub.nds.tls.subject;

import de.rub.nds.tls.subject.constants.TransportType;

public class HostInfo {

    private final String ip;
    private final String hostname;
    private Integer port;
    private final TransportType type;

    public HostInfo(String ip, String hostname, int port, TransportType type) {
        this.ip = ip;
        this.hostname = hostname;
        this.port = port;
        this.type = type;
    }

    public HostInfo(String hostname, int port, TransportType transportType) {
        this.port = port;
        if (hostname == null) {
            this.hostname = "127.0.0.42";
        } else {
            this.hostname = hostname;
        }
        if (hostname == null) {
            this.ip = "127.0.0.42";
        } else {
            this.ip = hostname;
        }
        this.type = transportType;
    }

    public TransportType getType() {
        return type;
    }

    public String getIp() {
        return ip;
    }

    public String getHostname() {
        return hostname;
    }

    public void updatePort(int port) {
        this.port = port;
    }

    public Integer getPort() {
        return port;
    }
}
