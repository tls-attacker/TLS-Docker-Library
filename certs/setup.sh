#!/bin/bash
cd "$(dirname "$0")" || exit 1
set -eu

docker build -t certs -f Dockerfile .

docker volume remove cert-data || true
docker volume create cert-data
docker run --rm -v cert-data:/cert/ certs cp -r ./. /cert/

echo "Copying Root CA Certificate to relevant image folders"
docker run --rm -d -v cert-data:/cert/ --name tmp-certs certs sleep 10
docker cp tmp-certs:/cert/ca.pem ../images/baseimage/
docker cp tmp-certs:/cert/ca.pem ../images/firefox/

if [ -d ./out ]
then
    rm -r ./out
fi
docker cp tmp-certs:/cert/ ./out
docker cp tmp-certs:/cert/keys.jks ./keys.jks
docker kill tmp-certs
