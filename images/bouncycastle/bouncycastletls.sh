#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

array=(50 51 52 53 54 55 56 57 58)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: BouncyCastleTLS 1.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}bouncycastletls-server:1_${array[$i]} -f Dockerfile-1_x .
	i=i+1
done

exit "$EXITCODE"
