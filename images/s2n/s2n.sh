#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

_docker build --build-arg VERSION=latest -t ${DOCKER_REPOSITORY}s2n-server:latest --target s2n-server .
_docker build --build-arg VERSION=latest -t ${DOCKER_REPOSITORY}s2n-client:latest --target s2n-client .

_docker build --build-arg VERSION=fips -t ${DOCKER_REPOSITORY}s2n-server:fips -f Dockerfile_fips --target s2n-server .
_docker build --build-arg VERSION=fips -t ${DOCKER_REPOSITORY}s2n-client:fips -f Dockerfile_fips --target s2n-client .


exit "$EXITCODE"
