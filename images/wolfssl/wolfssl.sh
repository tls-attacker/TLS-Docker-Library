#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

array=(4.0.0-stable 4.1.0-stable 4.2.0-stable 4.2.0c 4.3.0-stable 4.4.0-stable 4.5.0-stable 4.6.0-stable 4.7.0-stable 4.8.0-stable 4.8.1-stable 5.0.0-stable 5.1.0-stable 5.2.0-stable 5.3.0-stable 5.4.0-stable 5.5.0-stable 5.5.1-stable 5.5.2-stable 5.5.3-stable  5.5.4-stable)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: WolfSSL ${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}wolfssl-server:${array[$i]} -f Dockerfile-4_and_5_x --target wolfssl-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}wolfssl-client:${array[$i]} -f Dockerfile-4_and_5_x --target wolfssl-client .
	i=i+1
done

array=(15.8 15.7-stable 15.6 15.5a 15.5-stable 15.3-stable 15.0-stable 14.5 14.4 14.2 14.0b 14.0a 14.0-stable 13.3 13.2 13.0-stable 12.2-stable 12.0-stable 11.0-stable 10.4 10.3 10.2-stable 10.0a 10.0-stable 9.10b 9.10-stable 9.8 9.6w 9.6 9.1 9.0 7.0 6.9d 6.9c 6.9b 6.9 6.8 6.6 6.2 6.0b 6.0 4.8 4.6 4.2 4.0 3.3)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: WolfSSL 3.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}wolfssl-server:3.${array[$i]} -f Dockerfile-3_3-x --target wolfssl-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}wolfssl-client:3.${array[$i]} -f Dockerfile-3_3-x --target wolfssl-client .
	i=i+1
done

array=(3.2 3.0 2.6 2.4 2.0 1.0 0.2 0.0)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: WolfSSL 3.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}wolfssl-server:3.${array[$i]} -f Dockerfile-3_0-2 --target wolfssl-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}wolfssl-client:3.${array[$i]} -f Dockerfile-3_0-2 --target wolfssl-client .
	i=i+1
done

exit "$EXITCODE"
