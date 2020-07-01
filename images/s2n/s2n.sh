#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh


versions=(0.10.7 0.10.6 0.10.5 0.10.4 0.10.3 0.10.2 0.10.1 0.10.0 0.9.0)
for i in "${versions[@]}"; do
    _docker build --build-arg VERSION=${i} -t ${DOCKER_REPOSITORY}s2n-server:${i} --target s2n-server .
    _docker build --build-arg VERSION=${i} -t ${DOCKER_REPOSITORY}s2n-client:${i} --target s2n-client .
done

exit "$EXITCODE"
