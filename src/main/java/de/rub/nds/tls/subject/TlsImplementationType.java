/*
 * TLS-Docker-Library - A collection of open source TLS clients and servers
 *
 * Copyright 2017-2022 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tls.subject;

public enum TlsImplementationType {
    BEARSSL,
    BORINGSSL,
    BOTAN,
    BOUNCYCASTLE,
    CRYPTO_COMPLY,
    CRYPTLIB,
    DAMNVULNERABLEOPENSSL,
    FIREFOX,
    GNUTLS,
    JSSE,
    GO,
    LIBRESSL,
    MATRIXSSL,
    MBEDTLS,
    NSS,
    OCAMLTLS,
    OPENSSL,
    RUSTLS,
    S2N,
    SCHANNEL,
    SECURE_TRANSPORT,
    TLSLITE_NG,
    WOLFSSL,
    ERLANG,
    CURL;

    public static TlsImplementationType fromString(String type) {
        for (TlsImplementationType i : TlsImplementationType.values()) {
            if (i.name().toLowerCase().equals(type.toLowerCase())) {
                return i;
            }
        }
        return null;
    }
}
