<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<parameterProfile>
    <name>gnutls_default</name>
    <description>Default Profile for GnuTLS</description>
    <type>GNUTLS</type>
    <role>CLIENT</role>
    <Parameter>
        <cmdParameter>--no-ca-verification</cmdParameter>
        <type>INSECURE</type>
    </Parameter>
    <Parameter>
        <cmdParameter>--x509cafile [cert]</cmdParameter>
        <type>CA_CERTIFICATE</type>
    </Parameter>
    <Parameter>
        <cmdParameter>-p [port] [host]</cmdParameter>
        <type>HOST_PORT</type>
    </Parameter>
</parameterProfile>
