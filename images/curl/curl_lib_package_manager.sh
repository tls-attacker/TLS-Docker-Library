#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh


curl_versions=$(cat curl_versions.txt)
for CURL_VERSION in $curl_versions
do
    for base in "ubuntu:16.04" "debian:8" "debian:9"
    do
        _docker build \
            --build-arg BASE_IMG="$base" \
            --build-arg PREINSTALLCMD="apt-get update" \
            --build-arg INSTALLCMD="apt-get install -y libssl-dev" \
            --build-arg INSTALLCMD_BUILDTOOLS="apt-get install -y wget binutils gcc build-essential" \
            --build-arg VERSION="$CURL_VERSION" \
            --build-arg CURL_FLAG="--with-ssl" \
            --build-arg VERSION_LABEL="${CURL_VERSION}_openssl_${base/:/_}" \
            -t "${DOCKER_REPOSITORY}curl:${CURL_VERSION}_openssl_${base/:/_}" \
            -f Dockerfile_lib_package_manager \
            --target curl \
            .

            if [ ! -z "$DOCKER_REPOSITORY" ]; then
                docker push "${DOCKER_REPOSITORY}curl:${CURL_VERSION}_openssl_${base/:/_}"
            fi
    done
done
