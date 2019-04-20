package de.rub.nds.tls.subject;

public class HostInfo {

    private final String ip;
    private final String hostname;
    private int port;

    public HostInfo(String ip, String hostname, int port) {
        this.ip = ip;
        this.hostname = hostname;
        this.port = port;
    }

    public HostInfo(String hostname, int port) {
        this.ip = null;
        this.hostname = null;
        this.port = port;
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

    public int getPort() {
        return port;
    }
}
