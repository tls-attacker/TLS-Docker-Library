package de.rub.nds.tls.subject.instance;

public interface TlsInstance {
    void start();

    void stop();

    void stop(int secondsToWaitBeforeKilling);

    void kill();

    void restart();

    void remove();

    String getId();

    String getLogs() throws InterruptedException;

    long getExitCode();

    @Override
    String toString();

    void close();

}