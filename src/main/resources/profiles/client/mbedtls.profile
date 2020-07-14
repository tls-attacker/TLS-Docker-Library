<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<parameterProfile>
    <name>mbed_default</name>
    <description>Default Profile for MbedTLS</description>
    <type>MBEDTLS</type>
    <role>CLIENT</role>
    <Parameter>
        <cmdParameter>server_name=[host] server_port=[port]</cmdParameter>
        <type>HOST_PORT</type>
    </Parameter>
    <Parameter>
        <cmdParameter>ca_file=[cert]</cmdParameter>
        <type>CA_CERTIFICATE</type>
    </Parameter>
    <Parameter>
        <cmdParameter>auth_mode=none</cmdParameter>
        <type>INSECURE</type>
    </Parameter>
</parameterProfile>
