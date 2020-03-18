#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

_docker build --build-arg VERSION=4 -t bearssl-0_4-server -f Dockerfile-0_x --target bearssl-server .
_docker build --build-arg VERSION=4 -t bearssl-0_4-client -f Dockerfile-0_x --target bearssl-client .

_docker build --build-arg VERSION=5 -t bearssl-0_5-server -f Dockerfile-0_x --target bearssl-server .
_docker build --build-arg VERSION=5 -t bearssl-0_5-client -f Dockerfile-0_x --target bearssl-client .

exit "$EXITCODE"
