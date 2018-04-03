<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<parameterProfile>
    <name>bouncycastle_default</name>
    <description>Default Profile for Bouncycastle</description>
    <type>BOUNCYCASTLE</type>
    <role>SERVER</role>
    <Parameter>
        <cmdParameter>[port]</cmdParameter>
        <type>HOST_PORT</type>
    </Parameter>
    <Parameter>
        <cmdParameter>[keys] password rsa2048 [keys] password ec256</cmdParameter>
        <type>JKS_CERTIFICATE_KEY</type>
    </Parameter>
</parameterProfile>
