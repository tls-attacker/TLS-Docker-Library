<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<parameterProfile>
    <name>boring_default</name>
    <description>Default Profile for BoringSSL</description>
    <type>BORINGSSL</type>
    <role>SERVER</role>
    <Parameter>
        <cmdParameter>-accept [port]</cmdParameter>
        <type>HOST_PORT</type>
    </Parameter>
    <Parameter>
        <cmdParameter>-loop</cmdParameter>
        <type>LOOP</type>
    </Parameter>
    <Parameter>
        <cmdParameter>-cert [cert] -key [key]</cmdParameter>
        <type>CERTIFICATE_KEY</type>
    </Parameter>
</parameterProfile>
