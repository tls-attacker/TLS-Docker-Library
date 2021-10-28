#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

array=(0.14.2 0.14.3 1.0.0 1.1.0 1.1.2 1.1.3 1.1.4 1.1.5 1.1.6 1.1.7 1.1.8)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: polarssl ${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}mbedtls-server:${array[$i]} -f Dockerfile-polarssl_under_1.2 --target mbed-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}mbedtls-client:${array[$i]} -f Dockerfile-polarssl_under_1.2 --target mbed-client .
	if [ ! -z "$DOCKER_REPOSITORY" ]; then
		_docker push ${DOCKER_REPOSITORY}mbedtls-server:${array[$i]}
		_docker push ${DOCKER_REPOSITORY}mbedtls-client:${array[$i]}
	fi
	i=i+1
done

array=(1.2.0 1.2.1 1.2.2 1.2.3 1.2.4 1.2.5 1.2.6 1.2.7 1.2.8 1.2.9 1.2.10 1.2.11 1.2.12 1.2.13 1.2.14 1.2.15 1.2.16 1.2.17 1.2.18 1.2.19 1.3.0 1.3.1 1.3.2 1.3.3 1.3.4 1.3.5 1.3.6 1.3.7 1.3.8 1.3.9)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: polarssl ${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}mbedtls-server:${array[$i]} -f Dockerfile-polarssl_x --target mbed-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}mbedtls-client:${array[$i]} -f Dockerfile-polarssl_x --target mbed-client .
	if [ ! -z "$DOCKER_REPOSITORY" ]; then
		_docker push ${DOCKER_REPOSITORY}mbedtls-server:${array[$i]}
		_docker push ${DOCKER_REPOSITORY}mbedtls-client:${array[$i]}
	fi
	i=i+1
done

exit "$EXITCODE"
