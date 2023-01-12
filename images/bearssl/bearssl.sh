#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

# build master when required since releases are rare
_docker build --build-arg VERSION=master -t ${DOCKER_REPOSITORY}bearssl-server:master -f Dockerfile --target bearssl-server .
_docker build --build-arg VERSION=master -t ${DOCKER_REPOSITORY}bearssl-client:master -f Dockerfile --target bearssl-client .

# release versions
versions=(0.4 0.5 0.6)
for i in "${versions[@]}"; do
    _docker build --build-arg VERSION=v${i} -t ${DOCKER_REPOSITORY}bearssl-server:${i} -f Dockerfile --target bearssl-server .
    _docker build --build-arg VERSION=v${i} -t ${DOCKER_REPOSITORY}bearssl-client:${i} -f Dockerfile --target bearssl-client .
done

exit "$EXITCODE"
