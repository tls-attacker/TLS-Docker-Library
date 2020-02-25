#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

_docker build --build-arg VERSION=0 -t botan-2_0_0-server -f Dockerfile-2_0_x --target botan-server .
_docker build --build-arg VERSION=0 -t botan-2_0_0-client -f Dockerfile-2_0_x --target botan-client .

_docker build --build-arg VERSION=1 -t botan-2_0_1-server -f Dockerfile-2_0_x --target botan-server .
_docker build --build-arg VERSION=1 -t botan-2_0_1-client -f Dockerfile-2_0_x --target botan-client .

_docker build --build-arg VERSION=0 -t botan-2_1_0-server -f Dockerfile-2_1_x --target botan-server .
_docker build --build-arg VERSION=0 -t botan-2_1_0-client -f Dockerfile-2_1_x --target botan-client .

_docker build --build-arg VERSION=0 -t botan-2_2_0-server -f Dockerfile-2_2_x --target botan-server .
_docker build --build-arg VERSION=0 -t botan-2_2_0-client -f Dockerfile-2_2_x --target botan-client .

_docker build -t botan-2_3_0-server -f Dockerfile-2_3_0 --target botan-server .
_docker build -t botan-2_3_0-client -f Dockerfile-2_3_0 --target botan-client .

exit "$EXITCODE"