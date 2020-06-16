#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

_docker build -t ${DOCKER_REPOSITORY}ocamltls-server:0.8.0 --target ocamltls-server .
_docker build -t ${DOCKER_REPOSITORY}ocamltls-client:0.8.0 --target ocamltls-client .

exit "$EXITCODE"
