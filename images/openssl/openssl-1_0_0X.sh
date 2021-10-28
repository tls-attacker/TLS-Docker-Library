#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

array=(a b c d e f g h i j k l m n o p q r s -beta5)
typeset -i i=0 max=${#array[*]}
_docker build --build-arg VERSION= -t ${DOCKER_REPOSITORY}openssl-server:1.0.0 -f Dockerfile-1_0_0x --target openssl-server .
_docker build --build-arg VERSION= -t ${DOCKER_REPOSITORY}openssl-client:1.0.0 -f Dockerfile-1_0_0x --target openssl-client .
if [ ! -z "$DOCKER_REPOSITORY" ]; then
	_docker push ${DOCKER_REPOSITORY}openssl-server:1.0.0
	_docker push ${DOCKER_REPOSITORY}openssl-client:1.0.0
fi
while (( i < max ))
do
	echo "Feld $i: Openssl 1.0.0${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}openssl-server:1.0.0${array[$i]} -f Dockerfile-1_0_0x --target openssl-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}openssl-client:1.0.0${array[$i]} -f Dockerfile-1_0_0x --target openssl-client .
	if [ ! -z "$DOCKER_REPOSITORY" ]; then
		_docker push ${DOCKER_REPOSITORY}openssl-server:1.0.0${array[$i]}
		_docker push ${DOCKER_REPOSITORY}openssl-client:1.0.0${array[$i]}
	fi
	i=i+1
done
_docker build --build-arg VERSION=-beta1 -t ${DOCKER_REPOSITORY}openssl-server:1.0.0beta1 -f Dockerfile-1_0_0beta1-4 --target openssl-server .
_docker build --build-arg VERSION=-beta1 -t ${DOCKER_REPOSITORY}openssl-client:1.0.0beta1 -f Dockerfile-1_0_0beta1-4 --target openssl-client .
if [ ! -z "$DOCKER_REPOSITORY" ]; then
	_docker push ${DOCKER_REPOSITORY}openssl-server:1.0.0beta1
	_docker push ${DOCKER_REPOSITORY}openssl-client:1.0.0beta1
fi

_docker build --build-arg VERSION=-beta2 -t ${DOCKER_REPOSITORY}openssl-server:1.0.0beta2 -f Dockerfile-1_0_0beta1-4 --target openssl-server .
_docker build --build-arg VERSION=-beta2 -t ${DOCKER_REPOSITORY}openssl-client:1.0.0beta2 -f Dockerfile-1_0_0beta1-4 --target openssl-client .
if [ ! -z "$DOCKER_REPOSITORY" ]; then
	_docker push ${DOCKER_REPOSITORY}openssl-server:1.0.0beta2
	_docker push ${DOCKER_REPOSITORY}openssl-client:1.0.0beta2
fi

_docker build --build-arg VERSION=-beta3 -t ${DOCKER_REPOSITORY}openssl-server:1.0.0beta3 -f Dockerfile-1_0_0beta1-4 --target openssl-server .
_docker build --build-arg VERSION=-beta3 -t ${DOCKER_REPOSITORY}openssl-client:1.0.0beta3 -f Dockerfile-1_0_0beta1-4 --target openssl-client .
if [ ! -z "$DOCKER_REPOSITORY" ]; then
	_docker push ${DOCKER_REPOSITORY}openssl-server:1.0.0beta3
	_docker push ${DOCKER_REPOSITORY}openssl-client:1.0.0beta3
fi

_docker build --build-arg VERSION=-beta4 -t ${DOCKER_REPOSITORY}openssl-server:1.0.0beta4 -f Dockerfile-1_0_0beta1-4 --target openssl-server .
_docker build --build-arg VERSION=-beta4 -t ${DOCKER_REPOSITORY}openssl-client:1.0.0beta4 -f Dockerfile-1_0_0beta1-4 --target openssl-client .
if [ ! -z "$DOCKER_REPOSITORY" ]; then
	_docker push ${DOCKER_REPOSITORY}openssl-server:1.0.0beta4
	_docker push ${DOCKER_REPOSITORY}openssl-client:1.0.0beta4
fi

exit "$EXITCODE"
