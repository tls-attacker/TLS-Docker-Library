package de.rub.nds.tls.subject;

/**
 * The representation of a TLS-Instance used for a Test
 */
public interface TlsInstance {

    HostInfo getHostInfo();

    String getId();

    ConnectionRole getConnectionRole();

    String getHost();

    Integer getPort();

    String getName();

    String getLogs();

    long getExitCode();

    String getExitInfo();

    void setExitCode(long exitCode);

    void kill();

    void restart();

    @Override
    String toString();

    void start();

    void stop();

    void setPort(Integer port);
}
