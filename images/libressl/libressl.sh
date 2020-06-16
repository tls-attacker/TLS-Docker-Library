#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

#version 2.8.x
array=(0 1 2)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
        echo "Building: LibreSSL 2.8.${array[$i]}"
        _docker build --build-arg VERSION=8.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-server:2_8_${array[$i]} -f Dockerfile-2_x .
        i=i+1
done

#version 2.7.x
array=(0 1 2 3 4)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
        echo "Building: LibreSSL 2.7.${array[$i]}"
        _docker build --build-arg VERSION=7.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-server:2_7_${array[$i]} -f Dockerfile-2_x .
        i=i+1
done

#version 2.6.x
array=(0 1 2 3 4 5)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: LibreSSL 2.6.${array[$i]}"
	_docker build --build-arg VERSION=6.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-server:2_6_${array[$i]} -f Dockerfile-2_x libressl-server .
	_docker build --build-arg VERSION=6.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-client:2_6_${array[$i]} -f Dockerfile-2_x libressl-client .
	i=i+1
done

#version 2.5.x
array=(0 1 2 3 4 5)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: LibreSSL 2.5.${array[$i]}"
	_docker build --build-arg VERSION=5.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-server:2_5_${array[$i]} -f Dockerfile-2_x libressl-server .
	_docker build --build-arg VERSION=5.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-client:2_5_${array[$i]} -f Dockerfile-2_x libressl-client .
	i=i+1
done

#version 2.4.x
array=(0 1 2 3 4 5)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: LibreSSL 2.4.${array[$i]}"
	_docker build --build-arg VERSION=4.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-server:2_4_${array[$i]} -f Dockerfile-2_x libressl-server .
	_docker build --build-arg VERSION=4.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-client:2_4_${array[$i]} -f Dockerfile-2_x libressl-client .
	i=i+1
done

#version 2.3.x
array=(0 1 2 3 4 5 6 7 8 9 10)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: LibreSSL 2.3.${array[$i]}"
	_docker build --build-arg VERSION=3.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-server:2_3_${array[$i]} -f Dockerfile-2_x libressl-server .
	_docker build --build-arg VERSION=3.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-client:2_3_${array[$i]} -f Dockerfile-2_x libressl-client .
	i=i+1
done

#version 2.2.x
array=(0 1 2 3 4 5 6 7 8 9)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: LibreSSL 2.2.${array[$i]}"
	_docker build --build-arg VERSION=2.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-server:2_2_${array[$i]} -f Dockerfile-2_x --target libressl-server .
	_docker build --build-arg VERSION=2.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-client:2_2_${array[$i]} -f Dockerfile-2_x --target libressl-client .
	i=i+1
done

#version 2.1.x
array=(0 1 2 3 4 5 6 7 8 9 10)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: LibreSSL 2.1.${array[$i]}"
	_docker build --build-arg VERSION=1.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-server:2_1_${array[$i]} -f Dockerfile-2_x --target libressl-server .
	_docker build --build-arg VERSION=1.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-client:2_1_${array[$i]} -f Dockerfile-2_x --target libressl-client .
	i=i+1
done

#version 2.0.x
array=(0 1 2 3 4 5 6)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: LibreSSL 2.0.${array[$i]}"
	_docker build --build-arg VERSION=0.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-server:2_0_${array[$i]} -f Dockerfile-2_x --target libressl-server .
	_docker build --build-arg VERSION=0.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-client:2_0_${array[$i]} -f Dockerfile-2_x --target libressl-client .
	i=i+1
done

exit "$EXITCODE"
