#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

_docker build -t ${DOCKER_REPOSITORY}rustls:latest .


exit "$EXITCODE"
