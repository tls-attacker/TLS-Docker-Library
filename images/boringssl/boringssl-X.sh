#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh
# Script for building boringssl versions
# >2214 supports servermode
# shim is not working for 2272 2311 2357
array=(2272 2311 2357)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: BoringSSL ${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}boringssl-server:${array[$i]} -f Dockerfile-2272-2357 --target boringssl-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}boringssl-client:${array[$i]} -f Dockerfile-2272-2357 --target boringssl-client .
	i=i+1
done

array=(2490 2564 2623 2661)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: BoringSSL ${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}boringssl-server:${array[$i]} -f Dockerfile-2214-2661 --target boringssl-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}boringssl-client:${array[$i]} -f Dockerfile-2214-2661 --target boringssl-client .
	i=i+1
done

array=(2704 2883 2924 2987 3029 3112 3202 3239 3282 3359 3538 3945 chromium-stable master)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: BoringSSL ${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}boringssl-server:${array[$i]} -f Dockerfile-x --target boringssl-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}boringssl-client:${array[$i]} -f Dockerfile-x --target boringssl-client .
	i=i+1
done

_docker build -t ${DOCKER_REPOSITORY}boringssl-server:master-eval -f Dockerfile-master-eval --target boringssl-server .
_docker build -t ${DOCKER_REPOSITORY}boringssl-client:master-eval -f Dockerfile-master-eval --target boringssl-client .

_docker build -t ${DOCKER_REPOSITORY}boringssl-server:chromium-stable-eval -f Dockerfile-chromium-stable-eval --target boringssl-server .
_docker build -t ${DOCKER_REPOSITORY}boringssl-client:chromium-stable-eval -f Dockerfile-chromium-stable-eval --target boringssl-client .

exit "$EXITCODE"
