<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<parameterProfile>
    <name>wolfssl_pre_4_x</name>
    <description>Profile for Wolfssl pre 4.x</description>
    <type>WOLFSSL</type>
    <role>CLIENT</role>
    <Parameter>
        <cmdParameter>-h [host] -p [port]</cmdParameter>
        <type>HOST_PORT</type>
    </Parameter>
    <Parameter>
        <cmdParameter>-A [cert]</cmdParameter>
        <type>CA_CERTIFICATE</type>
    </Parameter>
    <Parameter>
        <cmdParameter>-d</cmdParameter>
        <type>INSECURE</type>
    </Parameter>
    <Version>[2-3]\..*</Version>
</parameterProfile>
