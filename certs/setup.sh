#!/bin/bash
cd "$(dirname "$0")" || exit 1
set -eu

docker build -t certs -f Dockerfile .

docker volume remove cert-data
docker volume create cert-data
docker run --rm -v cert-data:/cert/ certs cp -r ./. /cert/

echo "Copying Root CA Certificate to relevant image folders"
docker run --rm -v cert-data:/cert/ -v $(pwd)/../images:/dst/ debian /bin/bash -c "cp /cert/ca.pem /dst/baseimage/ && cp /cert/ca.pem /dst/firefox/"
