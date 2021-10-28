#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh


for base in "ubuntu:16.04" "debian:8" "debian:9"
do
    _docker build \
        --build-arg BASE_IMG="$base" \
        --build-arg PREINSTALLCMD="apt-get update" \
        --build-arg INSTALLCMD="apt-get install -y" \
        --build-arg VERSION_LABEL="${base/:/_}" \
        -t "${DOCKER_REPOSITORY}curl:${base/:/_}" \
        -f Dockerfile_package_manager \
        --target curl \
        .
done

for base in "alpine"
do
    _docker build \
        --build-arg BASE_IMG="$base" \
        --build-arg INSTALLCMD="apk add" \
        --build-arg VERSION_LABEL="${base/:/_}" \
        -t "${DOCKER_REPOSITORY}curl:${base/:/_}" \
        -f Dockerfile_package_manager \
        --target curl \
        .
done
