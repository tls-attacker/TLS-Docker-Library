<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<parameterProfile>
    <name>gnutls_default</name>
    <description>Default Profile for GnuTLS</description>
    <type>GNUTLS</type>
    <role>SERVER</role>
    <Parameter>
        <cmdParameter>--x509certfile [cert] --x509keyfile [key]</cmdParameter>
        <type>CERTIFICATE_KEY</type>
    </Parameter>
    <Parameter>
        <cmdParameter>--disable-client-cert</cmdParameter>
        <type>NO_CLIENT_AUTHENTICATION</type>
    </Parameter>
</parameterProfile>
