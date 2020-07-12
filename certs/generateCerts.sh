#!/bin/bash
set -eu

openssl genrsa -out ca_key.pem 2048
echo "Generating Root CA Certificate"
openssl req -new -nodes -x509 -subj "/C=DE/ST=NRW/L=Bochum/O=RUB/OU=NDS" -key ca_key.pem -out ca.pem -days 1024
echo "Generating RSA keys"
openssl genpkey -algorithm RSA -out rsa2048key.pem -pkeyopt rsa_keygen_bits:2048
openssl req -new -nodes -subj "/C=DE/ST=NRW/L=Bochum/O=RUB/OU=NDS/CN=example.com" -key rsa2048key.pem -out rsa2048cert.csr
openssl x509 -req -in rsa2048cert.csr -CA ca.pem -CAkey ca_key.pem -CAcreateserial -out rsa2048cert.pem -days 1024
cat rsa2048key.pem rsa2048cert.pem > rsa2048combined.pem
echo "Generating EC keys"
openssl genpkey -algorithm EC -out ec256key.pem -pkeyopt ec_paramgen_curve:P-256 -pkeyopt ec_param_enc:named_curve
openssl req -new -nodes -subj "/C=DE/ST=NRW/L=Bochum/O=RUB/OU=NDS/CN=example.com" -key ec256key.pem -out ec256cert.csr
openssl x509 -req -in ec256cert.csr -CA ca.pem -CAkey ca_key.pem -CAcreateserial -out ec256cert.pem -days 1024
cat ec256key.pem ec256cert.pem > ec256combined.pem
echo "Creating DH parameters"
openssl dhparam -out dh.pem 2048
echo "Creating db"
mkdir db
openssl pkcs12 -export -in rsa2048cert.pem -inkey rsa2048key.pem -out rsa2048.p12 -name cert -passin pass:password -passout pass:password
echo "Importing RSA key"
pk12util -i rsa2048.p12 -d db -K password -W password
openssl pkcs12 -export -in ec256cert.pem -inkey ec256key.pem -out ec256.p12 -name cert -passin pass:password -passout pass:password
echo "Importing EC key"
pk12util -i ec256.p12 -d db -K password -W password
echo "Creating Java keystore"
keytool -importkeystore -srckeystore rsa2048.p12 -srcstoretype pkcs12 -destkeystore keys.jks -deststoretype jks -alias cert -destalias rsa2048 -srcstorepass password -deststorepass password
keytool -importkeystore -srckeystore ec256.p12 -srcstoretype pkcs12 -destkeystore keys.jks -deststoretype jks -alias cert -destalias ec256 -srcstorepass password -deststorepass password
#use test-ca from rustls
curl -L https://github.com/ctz/rustls/tarball/master | tar zx --wildcards  --strip-components=1 '*/test-ca/'
