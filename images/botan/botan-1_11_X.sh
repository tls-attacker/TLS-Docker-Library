#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

# below 1.11.8 no executables are built
# 1.11.8
#   error during execution: Exception: Certificate_Store_In_Memory: FS access disabled
array=(9 10 11 12 13)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: Botan 1.11.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}botan-server:1.11.${array[$i]} -f Dockerfile-1_11_9-13 --target botan-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}botan-client:1.11.${array[$i]} -f Dockerfile-1_11_9-13 --target botan-client .
	i=i+1
done

array=(14 15 16 17 19 20 21 22 23 24 30 31 32 33)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: Botan 1.11.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}botan-server:1.11.${array[$i]} -f Dockerfile-1_11_x --target botan-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}botan-client:1.11.${array[$i]} -f Dockerfile-1_11_x --target botan-client .
	i=i+1
done

_docker build --build-arg VERSION=34 -t ${DOCKER_REPOSITORY}botan-server:1.11.34 -f Dockerfile-1_11_34 --target botan-server .
_docker build --build-arg VERSION=34 -t ${DOCKER_REPOSITORY}botan-client:1.11.34 -f Dockerfile-1_11_34 --target botan-client .

array=(25 26 27 28 29)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: Botan 1.11.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}botan-server:1.11.${array[$i]} -f Dockerfile-1_11_25-29 --target botan-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}botan-client:1.11.${array[$i]} -f Dockerfile-1_11_25-29 --target botan-client .
	i=i+1
done

exit "$EXITCODE"
