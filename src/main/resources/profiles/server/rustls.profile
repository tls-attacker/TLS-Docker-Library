<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<parameterProfile>
    <name>rustls_default</name>
    <description>Default Profile for rustls</description>
    <type>RUSTLS</type>
    <role>SERVER</role>
    <Parameter>
        <cmdParameter>--certs [cert] --key [key]</cmdParameter>
        <type>CERTIFICATE_KEY</type>
    </Parameter>
    <Parameter>
        <cmdParameter>-p [port] echo</cmdParameter>
        <type>HOST_PORT</type>
    </Parameter>
</parameterProfile>
