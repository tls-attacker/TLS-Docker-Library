#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

array=(a b c d e f g h i j k l -beta1 -beta2 -beta3 m n o p q r s t u)
typeset -i i=0 max=${#array[*]}
_docker build --build-arg VERSION= -t ${DOCKER_REPOSITORY}openssl-server:1_0_1 -f Dockerfile-1_0_1x --target openssl-server .
_docker build --build-arg VERSION= -t ${DOCKER_REPOSITORY}openssl-client:1_0_1 -f Dockerfile-1_0_1x --target openssl-client .
while (( i < 15 ))
do
	echo "Feld $i: Openssl 1.0.1${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}openssl-server:1_0_1${array[$i]} -f Dockerfile-1_0_1x --target openssl-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}openssl-client:1_0_1${array[$i]} -f Dockerfile-1_0_1x --target openssl-client .
	i=i+1
done

while (( i < max ))
do
	echo "Feld $i: Openssl 1.0.1${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}openssl-server:1_0_1${array[$i]} -f Dockerfile-1_0_1m-u --target openssl-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}openssl-client:1_0_1${array[$i]} -f Dockerfile-1_0_1m-u --target openssl-client .
	i=i+1
done

exit "$EXITCODE"
