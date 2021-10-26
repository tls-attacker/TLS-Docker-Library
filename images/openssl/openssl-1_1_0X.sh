#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

array=(a b c d e f g h i j k l)
typeset -i i=0 max=${#array[*]}
_docker build --build-arg VERSION= -t ${DOCKER_REPOSITORY}openssl-server:1.1.0 -f Dockerfile-1_1_0x --target openssl-server .
_docker build --build-arg VERSION= -t ${DOCKER_REPOSITORY}openssl-client:1.1.0 -f Dockerfile-1_1_0x --target openssl-client .
if [ ! -z "$DOCKER_REPOSITORY" ]; then
	docker push ${DOCKER_REPOSITORY}openssl-server:1.1.0
	docker push ${DOCKER_REPOSITORY}openssl-client:1.1.0
fi
while (( i < max ))
do
	echo "Feld $i: Openssl 1.1.0${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}openssl-server:1.1.0${array[$i]} -f Dockerfile-1_1_0x --target openssl-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}openssl-client:1.1.0${array[$i]} -f Dockerfile-1_1_0x --target openssl-client .
if [ ! -z "$DOCKER_REPOSITORY" ]; then
	docker push ${DOCKER_REPOSITORY}openssl-server:1.1.0${array[$i]}
	docker push ${DOCKER_REPOSITORY}openssl-client:1.1.0${array[$i]}
fi
	i=i+1
done

exit "$EXITCODE"
