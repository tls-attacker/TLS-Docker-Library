<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<parameterProfile>
    <name>mbed_default</name>
    <description>Default Profile for MbedTLS</description>
    <type>MBEDTLS</type>
    <role>SERVER</role>
    <Parameter>
        <cmdParameter>crt_file=[cert] key_file=[key] renegotiation=1</cmdParameter>
        <type>CERTIFICATE_KEY</type>
    </Parameter>
    <Parameter>
        <cmdParameter>server_port=[port]</cmdParameter>
        <type>HOST_PORT</type>
    </Parameter>
</parameterProfile>
