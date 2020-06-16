#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

array=(12.2-stable 12.0-stable 11.0-stable 10.4 10.3 10.2-stable 10.0a 10.0-stable 9.10b 9.10-stable 9.8 9.6w 9.6 9.1 9.0 7.0 6.9d 6.9c 6.9b 6.9 6.8 6.6 6.2 6.0b 6.0 4.8 4.6 4.2 4.0 3.3)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: WolfSSL 3.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}wolfssl-server:3.${array[$i]} -f Dockerfile-3_x --target wolfssl-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}wolfssl-client:3.${array[$i]} -f Dockerfile-3_x --target wolfssl-client .
	i=i+1
done

array=(3.2 3.0 2.6 2.4 2.0 1.0 0.2 0.0)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: WolfSSL 3.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}wolfssl-server:3.${array[$i]} -f Dockerfile-3_3_2 --target wolfssl-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}wolfssl-client:3.${array[$i]} -f Dockerfile-3_3_2 --target wolfssl-client .
	i=i+1
done

exit "$EXITCODE"
