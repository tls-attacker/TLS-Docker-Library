<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<parameterProfile>
    <name>openssl_pre_1_1_0</name>
    <description>Profile for Openssl pre 1.1.0</description>
    <type>OPENSSL</type>
    <role>SERVER</role>
    <Version>^1.0.*</Version>
    <Version>^0.9.*</Version>
    <Parameter>
        <cmdParameter>-accept [port]</cmdParameter>
        <type>HOST_PORT</type>
    </Parameter>
    <Parameter>
        <cmdParameter>-cert [cert] -key [key]</cmdParameter>
        <type>CERTIFICATE_KEY</type>
    </Parameter>
</parameterProfile>
