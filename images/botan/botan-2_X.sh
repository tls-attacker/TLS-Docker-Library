#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

versions=(0.0 0.1 1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0 9.0 10.0)
for i in "${versions[@]}"
do
    _docker build --build-arg VERSION=${i} -t ${DOCKER_REPOSITORY}botan-server:2.${i} -f Dockerfile-2_0-10 --target botan-server .
    _docker build --build-arg VERSION=${i} -t ${DOCKER_REPOSITORY}botan-client:2.${i} -f Dockerfile-2_0-10 --target botan-client .
done


versions=(11.0 12.0 12.1 13.0 14.0)
for i in "${versions[@]}"
do
    _docker build --build-arg VERSION=${i} -t ${DOCKER_REPOSITORY}botan-server:2.${i} -f Dockerfile-2_11-x --target botan-server .
    _docker build --build-arg VERSION=${i} -t ${DOCKER_REPOSITORY}botan-client:2.${i} -f Dockerfile-2_11-x --target botan-client .
done

exit "$EXITCODE"
