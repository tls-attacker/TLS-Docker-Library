#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

array=(2.2 2.1 1.0 0.2 0.1 0.0)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: Matrixssl 4.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}matrixssl-server:4.${array[$i]} -f Dockerfile-4_x --target matrixssl-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}matrixssl-client:4.${array[$i]} -f Dockerfile-4_x --target matrixssl-client .
	i=i+1
done

array=(9.5 9.3 9.1 9.0 8.7b 8.7a 8.7 8.6 8.4 8.3)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: Matrixssl 3.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}matrixssl-server:3.${array[$i]} -f Dockerfile-3_x --target matrixssl-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}matrixssl-client:3.${array[$i]} -f Dockerfile-3_x --target matrixssl-client .
	i=i+1
done

array=(7.2 4.0)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: Matrixssl 3.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}matrixssl-server:3.${array[$i]} -f Dockerfile-3_7_2_and_3_4_0 --target matrixssl-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}matrixssl-client:3.${array[$i]} -f Dockerfile-3_7_2_and_3_4_0 --target matrixssl-client .
	i=i+1
done



exit "$EXITCODE"
