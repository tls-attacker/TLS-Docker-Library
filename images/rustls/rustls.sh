#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

# Dependencies below 0.12.0 does not seem to be available anymore
# maybe a rust expert can do something here :D
versions=(0.17.0 0.15.1 0.15.0 0.14.0 0.13.1 0.13.0 0.12.0) # 0.11.0 0.10.0 0.9.0 0.8.0 0.7.0 0.6.0 0.5.8 0.5.7 0.5.6 0.5.5 0.5.4 0.5.3 0.5.2 0.5.1 0.5.0 0.1.2 0.1.1 0.1.0)
for i in "${versions[@]}"; do
    _docker build --build-arg VERSION=${i} -t ${DOCKER_REPOSITORY}rustls-client:${i} --target rustls-client .
    _docker build --build-arg VERSION=${i} -t ${DOCKER_REPOSITORY}rustls-server:${i} --target rustls-server .
done

versions=(0.16.0 0.15.2)
for i in "${versions[@]}"; do
    _docker build --build-arg VERSION=${i} -t ${DOCKER_REPOSITORY}rustls-client:${i} --target rustls-client -f Dockerfile_15_2-16_0 .
    _docker build --build-arg VERSION=${i} -t ${DOCKER_REPOSITORY}rustls-server:${i} --target rustls-server -f Dockerfile_15_2-16_0 .
done


exit "$EXITCODE"
