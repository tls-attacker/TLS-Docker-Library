#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

array=(50 51 52 53 54 55 56 57 58)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: BouncyCastleTLS 1.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}bouncycastle-server:1.${array[$i]} -f Dockerfile-1_x .
    if [ ! -z "$DOCKER_REPOSITORY" ]; then
      docker push ${DOCKER_REPOSITORY}bouncycastle-server:1.${array[$i]}
    fi
	i=i+1
done

exit "$EXITCODE"
