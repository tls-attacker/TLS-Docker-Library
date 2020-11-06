#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

if [ ! -f curl_versions.txt ]; then
    # generate curl versions if versions file does not exist
    {\
        curl -sS https://curl.haxx.se/download/archeology/ | grep -oP 'curl-[\.0-9a-zA-Z]+\.tar\.gz' | sed -E 's/curl-(.+)\.tar\.gz/\1/p' | uniq; \
        curl -sS https://curl.haxx.se/download/ | grep -oP 'curl-[\.0-9a-zA-Z]+\.tar\.gz' | sed -E 's/curl-(.+)\.tar\.gz/\1/p' | uniq ; \
    } > curl_versions.txt
fi
curl_versions=$(cat curl_versions.txt)

#example command: docker build --build-arg SSL_BASE=openssl-client:1.1.1g --build-arg VERSION=7.72.0 -t curl_test -f Dockerfile --target curl .

for CURL_VERSION in $curl_versions
do
    for versions_file in libversions/*.txt
    do
        # extract lib name (for tagging) from filename
        LIB_NAME=${versions_file#"libversions/"}
        LIB_NAME=${LIB_NAME%".txt"}
        # for library files the first line is the base image name
        # the following lines are version names
        SSL_BASE=$(head -n1 "$versions_file")
        CURL_FLAG=$(head -n2 "$versions_file" | tail -n1)
        lib_versions=$(tail -n+3 "$versions_file")
        for SSL_VERSION in $lib_versions
        do
            _docker build \
                --build-arg SSL_BASE="${DOCKER_REPOSITORY}$SSL_BASE:$SSL_VERSION" \
                --build-arg VERSION="$CURL_VERSION" \
                --build-arg CURL_FLAG="$CURL_FLAG" \
                --build-arg VERSION_LABEL="${CURL_VERSION}_${LIB_NAME}_${SSL_VERSION}" \
                -t "${DOCKER_REPOSITORY}curl:${CURL_VERSION}_${LIB_NAME}_${SSL_VERSION}" \
                -f Dockerfile \
                --target curl \
                .
        done
    done
done
