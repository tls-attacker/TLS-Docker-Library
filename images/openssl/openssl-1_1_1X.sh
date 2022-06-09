#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

array=(a b c d e f g h i m -pre1 -pre2 -pre3 -pre4 -pre5 -pre6 -pre7 -pre8 -pre9)
typeset -i i=0 max=${#array[*]}
_docker build --build-arg VERSION= -t ${DOCKER_REPOSITORY}openssl-server:1.1.1 -f Dockerfile-1_1_1x --target openssl-server .
_docker build --build-arg VERSION= -t ${DOCKER_REPOSITORY}openssl-client:1.1.1 -f Dockerfile-1_1_1x --target openssl-client .
while (( i < max ))
do
	echo "Feld $i: Openssl 1.1.1${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}openssl-server:1.1.1${array[$i]} -f Dockerfile-1_1_1x --target openssl-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}openssl-client:1.1.1${array[$i]} -f Dockerfile-1_1_1x --target openssl-client .
	i=i+1
done

exit "$EXITCODE"
