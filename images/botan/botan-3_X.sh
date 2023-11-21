#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

versions=(0.0-alpha0 0.0-alpha1)
for i in "${versions[@]}"
do
    _docker build --build-arg VERSION=${i} -t ${DOCKER_REPOSITORY}botan-server:3.${i} -f Dockerfile-3x --target botan-server .
    _docker build --build-arg VERSION=${i} -t ${DOCKER_REPOSITORY}botan-client:3.${i} -f Dockerfile-3x --target botan-client .
done
