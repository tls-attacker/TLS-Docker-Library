<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<parameterProfile>
    <name>rustls_default</name>
    <description>Default Profile for RusTLS</description>
    <type>RUSTLS</type>
    <role>CLIENT</role>
    <Parameter>
        <cmdParameter>--cafile [cert]</cmdParameter>
        <type>CA_CERTIFICATE</type>
    </Parameter>
    <Parameter>
        <cmdParameter>--insecure</cmdParameter>
        <type>INSECURE</type>
    </Parameter>
    <Parameter>
        <cmdParameter>-p [port] [host]</cmdParameter>
        <type>HOST_PORT</type>
    </Parameter>
</parameterProfile>
