#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

array=(9.4 9.2 9.1 9.0 8.6 8.5 8.5a 8.4 8.3 8.2 8.0 7.2 7.0 6.2 6.0 5.2b 5.0 4.7 4.6 4.2 4.0 3.0)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: CyaSSL 2.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}cyassl-server:2.${array[$i]} -f Dockerfile-2_x --target wolfssl-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}cyassl-client:2.${array[$i]} -f Dockerfile-2_x --target wolfssl-client .
	i=i+1
done

exit "$EXITCODE"
