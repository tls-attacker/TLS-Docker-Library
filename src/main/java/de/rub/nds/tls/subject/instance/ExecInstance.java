package de.rub.nds.tls.subject.instance;

public interface ExecInstance {
    boolean isRunning();

    void close();
}