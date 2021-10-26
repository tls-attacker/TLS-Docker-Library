#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh


versions=(0.10.24 0.10.23 0.10.22 0.10.21 0.10.20 0.10.19 0.10.18 0.10.17 0.10.16 0.10.15 0.10.14 0.10.13 0.10.12 0.10.11 0.10.10 0.10.9 0.10.8 0.10.7 0.10.6 0.10.5 0.10.4 0.10.3 0.10.2 0.10.1 0.10.0 0.9.0)
for i in "${versions[@]}"; do
    _docker build --build-arg VERSION=${i} -t ${DOCKER_REPOSITORY}s2n-server:${i} --target s2n-server .
    _docker build --build-arg VERSION=${i} -t ${DOCKER_REPOSITORY}s2n-client:${i} --target s2n-client .
    if [ ! -z "$DOCKER_REPOSITORY" ]; then
        docker push ${DOCKER_REPOSITORY}s2n-server:${i}
        docker push ${DOCKER_REPOSITORY}s2n-client:${i}
    fi
done

exit "$EXITCODE"
