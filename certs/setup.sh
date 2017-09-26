#!/bin/sh
if [ ! -f cert.pem ]; then
  if [ ! -f key.pem ]; then
    openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout key.pem -out cert.pem
  fi
fi
if [ ! -d db ]; then
  mkdir db
  openssl pkcs12 -export -in cert.pem -inkey key.pem -out server.p12 -name cert
  pk12util -i server.p12 -d db
fi

exit 0

docker volume create cert-data
docker build -t cert-tmp .
docker run --rm -v cert-data:/cert/ cert-tmp cp -r /src/cert.pem /src/key.pem /src/db/ /cert/
docker rmi cert-tmp
