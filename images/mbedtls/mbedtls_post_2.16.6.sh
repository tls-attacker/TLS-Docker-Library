#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

# https://github.com/ARMmbed/mbedtls/releases
array=(2.24.0)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: mbed TLS ${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}mbedtls-server:${array[$i]} -f Dockerfile-mbedtls_post_2.16.6 --target mbed-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}mbedtls-client:${array[$i]} -f Dockerfile-mbedtls_post_2.16.6 --target mbed-client .
	i=i+1
done

exit "$EXITCODE"
