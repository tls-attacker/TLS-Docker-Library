<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<parameterProfile>
    <name>wolfssl_pre_4_x</name>
    <description>Profile for Wolfssl pre 4_x</description>
    <type>WOLFSSL</type>
    <role>SERVER</role>
    <Parameter>
        <cmdParameter>-c [cert] -k [key]</cmdParameter>
        <type>CERTIFICATE_KEY</type>
    </Parameter>
    <Parameter>
        <cmdParameter>-d -b -x -i</cmdParameter>
        <type>NONE</type>
    </Parameter>
    <Parameter>
        <cmdParameter>-p [port]</cmdParameter>
        <type>HOST_PORT</type>
    </Parameter>
    <Version>[2-3]\..*</Version>
</parameterProfile>
