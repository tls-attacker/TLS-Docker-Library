package de.rub.nds.tls.subject;

public class HostInfo {

    private final String ip;
    private final String hostname;
    private Integer port;

    public HostInfo(String ip, String hostname, int port) {
        this.ip = ip;
        this.hostname = hostname;
        this.port = port;
    }

    public HostInfo(String hostname, int port) {
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
