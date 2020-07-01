<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<parameterProfile>
    <name>s2n_default</name>
    <description>Default Profile for s2n</description>
    <type>S2N</type>
    <role>CLIENT</role>
    <Parameter>
        <cmdParameter>-f [cert]</cmdParameter>
        <type>CA_CERTIFICATE</type>
    </Parameter>
    <Parameter>
        <cmdParameter>-i</cmdParameter>
        <type>INSECURE</type>
    </Parameter>
    <Parameter>
        <cmdParameter>[host] [port]</cmdParameter>
        <type>HOST_PORT</type>
    </Parameter>
</parameterProfile>
