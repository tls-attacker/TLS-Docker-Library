#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

array=(0.13.0)
typeset -i i=0 max=${#array[*]}

while (( i < max ))
do
	echo "Feld $i: python-mbedtls-${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}python_mbedtls-server:${array[$i]} -f Dockerfile .
	if [ ! -z "$DOCKER_REPOSITORY" ]; then
		_docker push ${DOCKER_REPOSITORY}python_mbedtls-server:${array[$i]}
	fi
	i=i+1
done

exit "$EXITCODE"
