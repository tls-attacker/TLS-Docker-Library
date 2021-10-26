#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh
exit_on_error

_docker build -t ${DOCKER_REPOSITORY}gotls:latest .
if [ ! -z "$DOCKER_REPOSITORY" ]; then
    docker push ${DOCKER_REPOSITORY}gotls:latest
fi

exit "$EXITCODE"
exit "$EXITCODE"
