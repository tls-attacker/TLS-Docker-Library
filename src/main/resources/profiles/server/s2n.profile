<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<parameterProfile>
    <name>s2n_default</name>
    <description>Default Profile for s2n</description>
    <type>S2N</type>
    <role>SERVER</role>
    <Parameter>
        <cmdParameter>--cert [cert] --key [key]</cmdParameter>
        <type>CERTIFICATE_KEY</type>
    </Parameter>
    <Parameter>
        <cmdParameter>--parallelize</cmdParameter>
        <type>PARALLELIZE</type>
        <description>Warning: this option is not compatible with TLS Resumption, since each thread gets its own Session cache.</description>
    </Parameter>
    <Parameter>
        <cmdParameter>--self-service-blinding [host] [port]</cmdParameter>
        <type>HOST_PORT</type>
        <description>--self-service-blinding: Do not introduce 10-30 second delays on TLS Handshake errors.</description>
    </Parameter>
</parameterProfile>
