<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<parameterProfile>
    <name>nss_default</name>
    <description>Default Profile for NSS</description>
    <type>NSS</type>
    <role>SERVER</role>
    <Parameter>
        <cmdParameter>-p [port]</cmdParameter>
        <type>HOST_PORT</type>
    </Parameter>
    <Parameter>
        <cmdParameter>-v -w password</cmdParameter>
        <type>NONE</type>
    </Parameter>
    <Parameter>
        <cmdParameter>-d [cert] -n [key]</cmdParameter>
        <type>CERTIFICATE_KEY</type>
    </Parameter>
</parameterProfile>
