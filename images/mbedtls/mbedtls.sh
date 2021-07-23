#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

# https://tls.mbed.org/download-archive
array=(1.3.10 1.3.11 1.3.12 1.3.13 1.3.14 1.3.15 1.3.16 1.3.17 1.3.18 1.3.19 1.3.20 1.3.21 1.3.22 2.0.0 2.1.0 2.1.1 2.1.2 2.1.3 2.1.4 2.1.5 2.1.6 2.1.7 2.1.8 2.1.9 2.1.10 2.1.11 2.1.12 2.1.13 2.1.14 2.1.15 2.1.16 2.1.17 2.1.18 2.2.0 2.2.1 2.3.0 2.4.0 2.4.2 2.5.1 2.6.0 2.7.0 2.7.2 2.7.3 2.7.4 2.7.5 2.7.6 2.7.7 2.7.8 2.7.9 2.7.10 2.7.11 2.7.12 2.7.13 2.7.14 2.7.15 2.7.16 2.7.17 2.7.18 2.8.0 2.9.0 2.11.0 2.12.0 2.13.0 2.14.0 2.14.1 2.16.0 2.16.1 2.16.2 2.16.3 2.16.4 2.16.5 2.16.6 2.16.7 2.16.8 2.16.9 2.17.0 2.18.0 2.18.1 2.19.0 2.19.1 2.20.0 2.21.0 2.22.0 2.23.0 2.24.0 2.25.0)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: mbed TLS ${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}mbedtls-server:${array[$i]} -f Dockerfile-mbedtls_x2 --target mbed-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}mbedtls-client:${array[$i]} -f Dockerfile-mbedtls_x2 --target mbed-client .
	i=i+1
done

exit "$EXITCODE"
