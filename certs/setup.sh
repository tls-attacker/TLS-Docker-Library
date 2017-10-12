#!/bin/sh
if [ ! -f ca.pem ] && [ ! -f ca_key.pem ]; then
  openssl req -new -nodes -x509 -subj "/C=DE/ST=NRW/L=Bochum/O=RUB/OU=NDS" -newkey rsa:2048 -keyout ca_key.pem -out ca.pem
fi
if [ ! -f cert.pem ] && [ ! -f key.pem ]; then
  openssl req -new -nodes -subj "/C=DE/ST=NRW/L=Bochum/O=RUB/OU=NDS/CN=example.com" -newkey rsa:2048 -keyout key.pem -out cert.csr
  openssl x509 -req -in cert.csr -CA ca.pem -CAkey ca_key.pem -CAcreateserial -out cert.pem -days 1024
fi
if [ ! -f dh.pem ]; then
  openssl dhparam -out dh.pem 2048
fi
if [ ! -d db ]; then
  mkdir db
  openssl pkcs12 -export -in cert.pem -inkey key.pem -out server.p12 -name cert
  pk12util -i server.p12 -d db
fi

docker volume create cert-data
docker build -t cert-tmp .
docker run --rm -v cert-data:/cert/ cert-tmp cp -r /src/cert.pem /src/key.pem /src/ca.pem /src/ca_key.pem /src/dh.pem /src/db/ /cert/
docker rmi cert-tmp
