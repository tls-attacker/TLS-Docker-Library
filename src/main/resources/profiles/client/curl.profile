<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<parameterProfile>
    <name>curl_default</name>
    <description>Default Profile for Curl</description>
    <type>CURL</type>
    <role>CLIENT</role>
    <Parameter>
        <cmdParameter>https://[host]:[port]/</cmdParameter>
        <type>HOST_PORT</type>
    </Parameter>
    <Parameter>
        <cmdParameter>-cacert [cert]</cmdParameter>
        <type>CA_CERTIFICATE</type>
    </Parameter>
    <Parameter>
        <cmdParameter>--insecure</cmdParameter>
        <type>INSECURE</type>
    </Parameter>
</parameterProfile>
