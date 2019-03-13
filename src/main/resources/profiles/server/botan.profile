<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<parameterProfile>
    <name>botan_default</name>
    <description>Default Profile for Botan</description>
    <type>BOTAN</type>
    <role>SERVER</role>
    <Parameter>
        <cmdParameter>[port]</cmdParameter>
        <type>HOST_PORT</type>
    </Parameter>
    <Parameter>
        <cmdParameter>[cert] [key]</cmdParameter>
        <type>CERTIFICATE_KEY</type>
    </Parameter>
    <Parameter>
        <cmdParameter>--policy=compat.txt</cmdParameter>
        <type>NONE</type>
    </Parameter>
</parameterProfile>
