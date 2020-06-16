#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

_docker build --build-arg VERSION=0 -t ${DOCKER_REPOSITORY}botan-server:2_0_0 -f Dockerfile-2_0_x --target botan-server .
_docker build --build-arg VERSION=0 -t ${DOCKER_REPOSITORY}botan-client:2_0_0 -f Dockerfile-2_0_x --target botan-client .

_docker build --build-arg VERSION=1 -t ${DOCKER_REPOSITORY}botan-server:2_0_1 -f Dockerfile-2_0_x --target botan-server .
_docker build --build-arg VERSION=1 -t ${DOCKER_REPOSITORY}botan-client:2_0_1 -f Dockerfile-2_0_x --target botan-client .

_docker build --build-arg VERSION=0 -t ${DOCKER_REPOSITORY}botan-server:2_1_0 -f Dockerfile-2_1_x --target botan-server .
_docker build --build-arg VERSION=0 -t ${DOCKER_REPOSITORY}botan-client:2_1_0 -f Dockerfile-2_1_x --target botan-client .

_docker build --build-arg VERSION=0 -t ${DOCKER_REPOSITORY}botan-server:2_2_0 -f Dockerfile-2_2_x --target botan-server .
_docker build --build-arg VERSION=0 -t ${DOCKER_REPOSITORY}botan-client:2_2_0 -f Dockerfile-2_2_x --target botan-client .

_docker build -t ${DOCKER_REPOSITORY}botan-server:2_3_0 -f Dockerfile-2_3_0 --target botan-server .
_docker build -t ${DOCKER_REPOSITORY}botan-client:2_3_0 -f Dockerfile-2_3_0 --target botan-client .

exit "$EXITCODE"
