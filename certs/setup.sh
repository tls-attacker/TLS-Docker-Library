#!/bin/bash
cd "$(dirname "$0")" || exit 1
set -eu

docker build -t certs -f Dockerfile .

docker volume remove cert-data || true
docker volume create cert-data
docker run --rm -v cert-data:/cert/ certs cp -r ./. /cert/

echo "Copying Root CA Certificate to relevant image folders"
docker run --rm -d -v cert-data:/cert/ --name tmp-certs certs sleep infinity
docker cp tmp-certs:/cert/ca.pem "$(pwd)/../images/baseimage/"
docker cp tmp-certs:/cert/ca.pem "$(pwd)/../images/firefox/"
docker cp tmp-certs:/cert/keys.jks "$(pwd)/keys.jks"
docker kill tmp-certs
