<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<parameterProfile>
    <name>mbed_default_pre_1_2_2</name>
    <description>Default Profile for MbedTLS before version 1.2.2</description>
    <type>MBED</type>
    <role>CLIENT</role>
    <Version>^1.0</Version>
    <Version>^1.1</Version>
    <Version>^1.2.[0-1]</Version>
    <Parameter>
        <cmdParameter>server_name=[host] server_port=[port]</cmdParameter>
        <type>HOST_PORT</type>
    </Parameter>
    <Parameter>
        <cmdParameter>ca_file=[cert]</cmdParameter>
        <type>CA_CERTIFICATE</type>
    </Parameter>
    <Parameter>
        <cmdParameter></cmdParameter>
        <type>INSECURE</type>
    </Parameter>
</parameterProfile>
