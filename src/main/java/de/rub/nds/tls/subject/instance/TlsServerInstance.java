package de.rub.nds.tls.subject.instance;

import de.rub.nds.tls.subject.HostInfo;

public interface TlsServerInstance extends TlsInstance {
    HostInfo getHostInfo();

    int getPort();

}