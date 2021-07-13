#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

array=(a b c d e f g h i j k l m n o p q r s t u)
typeset -i i=0 max=${#array[*]}
_docker build --build-arg VERSION= -t ${DOCKER_REPOSITORY}openssl-server:1.0.2 -f Dockerfile-1_0_2_and_betas --target openssl-server .
_docker build --build-arg VERSION= -t ${DOCKER_REPOSITORY}openssl-client:1.0.2 -f Dockerfile-1_0_2_and_betas --target openssl-client .

_docker build --build-arg VERSION=-beta1 -t ${DOCKER_REPOSITORY}openssl-server:1.0.2beta1 -f Dockerfile-1_0_2_and_betas --target openssl-server .
_docker build --build-arg VERSION=-beta1 -t ${DOCKER_REPOSITORY}openssl-client:1.0.2beta1 -f Dockerfile-1_0_2_and_betas --target openssl-client .

_docker build --build-arg VERSION=-beta2 -t ${DOCKER_REPOSITORY}openssl-server:1.0.2beta2 -f Dockerfile-1_0_2_and_betas --target openssl-server .
_docker build --build-arg VERSION=-beta2 -t ${DOCKER_REPOSITORY}openssl-client:1.0.2beta2 -f Dockerfile-1_0_2_and_betas --target openssl-client .

_docker build --build-arg VERSION=-beta3 -t ${DOCKER_REPOSITORY}openssl-server:1.0.2beta3 -f Dockerfile-1_0_2_and_betas --target openssl-server .
_docker build --build-arg VERSION=-beta3 -t ${DOCKER_REPOSITORY}openssl-client:1.0.2beta3 -f Dockerfile-1_0_2_and_betas --target openssl-client .

while (( i < max ))
do
	echo "Feld $i: Openssl 1.0.2${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}openssl-server:1.0.2${array[$i]} -f Dockerfile-1_0_2x --target openssl-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}openssl-client:1.0.2${array[$i]} -f Dockerfile-1_0_2x --target openssl-client .
	i=i+1
done

exit "$EXITCODE"
