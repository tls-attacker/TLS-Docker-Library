#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

array=(1.0-beta1 0.7 0.6 0.5 0.4 0.3 0.2 0.1 0.0)
typeset -i i=0 max=${#array[*]}
_docker build --build-arg VERSION= -t ${DOCKER_REPOSITORY}openssl-server:3 -f Dockerfile-3_x --target openssl-server .
_docker build --build-arg VERSION= -t ${DOCKER_REPOSITORY}openssl-client:3 -f Dockerfile-3_x --target openssl-client .
while (( i < max ))
do
	echo "Feld $i: Openssl 3.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}openssl-server:3.${array[$i]} -f Dockerfile-3_x --target openssl-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}openssl-client:3.${array[$i]} -f Dockerfile-3_x --target openssl-client .
	i=i+1
done

exit "$EXITCODE"
