<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<parameterProfile>
    <name>boringssl_default</name>
    <description>Default Profile for BoringSSL</description>
    <type>BORINGSSL</type>
    <role>CLIENT</role>
    <Parameter>
        <cmdParameter>-connect [host]:[port]</cmdParameter>
        <type>HOST_PORT</type>
    </Parameter>
    <Parameter>
        <cmdParameter>-root-certs [cert]</cmdParameter>
        <type>CA_CERTIFICATE</type>
    </Parameter>
    <Parameter>
        <cmdParameter></cmdParameter>
        <type>INSECURE</type>
    </Parameter>
</parameterProfile>
