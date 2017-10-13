#!/bin/bash

inc=5

fuzzerdir="../TLS-Fuzzer-Development"
serverscriptsdir="../TLS-Docker-Library/"
outputdir="$fuzzerdir/config/subject"
fuzzer="$fuzzerdir/target/EvolutionaryFuzzer-0.90.jar"


rm $outputdir/*

library=$1

if [[ -z $1 ]]
then
    select inp in "openssl" "matrixssl" "libressl" "gnutls" "wolfssl" "boringssl" "botan" "mbedtls" "s2n" "bearssl" "rustls" "bouncycastle" "ocamltls" "nss";
    do
        library=$inp;
        break;
    done
fi

case $library in
    openssl )
        openssldir="$serverscriptsdir/openssl/";
        java -jar $fuzzer new-client -increment $inc -output $outputdir/opensslclient -start "$openssldir/apps/openssl s_client -connect localhost:[port]" -port 4444;
        ;;

    matrixssl )
        matrixdir="$serverscriptsdir/matrixssl/";
        java -jar $fuzzer new-client -increment $inc -output $outputdir/matrixclient -start "$matrixdir/apps/ssl/client -s 127.0.0.1 -p [port]" -port 4444;
        ;;

    libressl )
        libressldir="$serverscriptsdir/libressl/";
        java -jar $fuzzer new-client -increment $inc -output $outputdir/libresslclient -start "$libressldir/apps/openssl/openssl s_client -connect localhost:[port]" -port 4444;
        ;;

    gnutls )
        gnutlsdir="$serverscriptsdir/gnutls/";
        java -jar $fuzzer new-client -increment $inc -output $outputdir/gnutlsclient -start "$gnutlsdir/src/gnutls-cli localhost -p [port]" -port 4444;
        ;;

    wolfssl )
        wolfssldir="$serverscriptsdir/wolfssl/";
        java -jar $fuzzer new-client -increment $inc -output $outputdir/wolfsslclient -start "$wolfssldir/examples/client/client localhost -p [port]" -port 4444;
        ;;

    boringssl )
        boringssldir="$serverscriptsdir/boringssl/";
        java -jar $fuzzer new-client -increment $inc -output $outputdir/boringsslclient -start "$boringssldir/build/tool/bssl client -connect localhost:[port]" -port 4444;
        ;;

    botan )
        botandir="$serverscriptsdir/botan/Botan-2.2.0";
        java -jar $fuzzer new-client -increment $inc -output $outputdir/botanclient -start "$botandir/botan tls_client localhost --port=[port]" -port 4444;
        ;;

    mbedtls )
        mbedtlsdir="$serverscriptsdir/mbedtls/mbedtls-2.6.0";
        java -jar $fuzzer new-client -increment $inc -output $outputdir/mbedtlsclient -start "$mbedtlsdir/build/programs/ssl/ssl_client2 server_name=localhost server_port=[port]" -port 4444;
        ;;
    s2n )
        s2ndir="$serverscriptsdir/s2n/";
        java -jar $fuzzer new-client -increment $inc -output $outputdir/mbedtlsclient -start "$mbedtlsdir/build/programs/ssl/ssl_client2 server_name=localhost server_port=[port]" -port 4444;
        ;;
    bearssl )
        bearssldir="$serverscriptsdir/bearssl/";
        java -jar $fuzzer new-client -increment $inc -output $outputdir/mbedtlsclient -start "$mbedtlsdir/build/programs/ssl/ssl_client2 server_name=localhost server_port=[port]" -port 4444;
        ;;
    rustls )
        rustlsdir="$serverscriptsdir/rustls/";
        java -jar $fuzzer new-client -increment $inc -output $outputdir/mbedtlsclient -start "$mbedtlsdir/build/programs/ssl/ssl_client2 server_name=localhost server_port=[port]" -port 4444;
        ;;
    bouncycastle )
        bouncycastledir="$serverscriptsdir/bouncycastle";
        java -jar $fuzzer new-client -increment $inc -output $outputdir/mbedtlsclient -start "$mbedtlsdir/build/programs/ssl/ssl_client2 server_name=localhost server_port=[port]" -port 4444;
        ;;
    ocamltls )
        ocamltlsdir="$serverscriptsdir/ocaml-tls";
        java -jar $fuzzer new-client -increment $inc -output $outputdir/mbedtlsclient -start "$mbedtlsdir/build/programs/ssl/ssl_client2 server_name=localhost server_port=[port]" -port 4444;
        ;;
    nss )
        nssdir="$serverscriptsdir/nss";
        java -jar $fuzzer new-client -increment $inc -output $outputdir/mbedtlsclient -start "$mbedtlsdir/build/programs/ssl/ssl_client2 server_name=localhost server_port=[port]" -port 4444;
        ;;
esac
