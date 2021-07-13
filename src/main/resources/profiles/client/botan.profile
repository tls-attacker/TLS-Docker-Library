<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<parameterProfile>
    <name>botan_default</name>
    <description>Default Profile for Botan</description>
    <type>BOTAN</type>
    <role>CLIENT</role>
    <Parameter>
        <cmdParameter>[host] --port=[port]</cmdParameter>
        <type>HOST_PORT</type>
    </Parameter>
    <Parameter>
        <cmdParameter>--skip-system-cert-store</cmdParameter>
        <type>INSECURE</type>
    </Parameter>
    <Parameter>
        <cmdParameter>--trusted-cas=[cert]</cmdParameter>
        <type>CA_CERTIFICATE</type>
    </Parameter>
</parameterProfile>
