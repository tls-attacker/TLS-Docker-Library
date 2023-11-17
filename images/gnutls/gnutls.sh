#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

array=(0 1 2 3 4 5 6 7 8)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: GnuTLS 3.7.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}gnutls-server:3.7.${array[$i]} -f Dockerfile-3_7_0-x --target gnutls-server --progress=plain .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}gnutls-client:3.7.${array[$i]} -f Dockerfile-3_7_0-x --target gnutls-client --progress=plain .
	i=i+1
done


# 3.6.6 and 3.6.5 does not compile due to strange errors
# "Nettle lacks the required rsa_sec_decrypt function"
array=(3 4 7 8 9 10 11 12 13 14 15 16)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: GnuTLS 3.6.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}gnutls-server:3.6.${array[$i]} -f Dockerfile-3_6_3-x --target gnutls-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}gnutls-client:3.6.${array[$i]} -f Dockerfile-3_6_3-x --target gnutls-client .
	i=i+1
done


array=(0 0_1 1 2)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: GnuTLS 3.6.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}gnutls-server:3.6.${array[$i]} -f Dockerfile-3_6_0-2 --target gnutls-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}gnutls-client:3.6.${array[$i]} -f Dockerfile-3_6_0-2 --target gnutls-client .
	i=i+1
done


array=(0 1 2 4 5 6 7)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: GnuTLS 3.5.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}gnutls-server:3.5.${array[$i]} -f Dockerfile-3_5_0-7 --target gnutls-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}gnutls-client:3.5.${array[$i]} -f Dockerfile-3_5_0-7 --target gnutls-client .
	i=i+1
done

array=(8 9 10 11 12 13 14 15 16 17 18 19)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: GnuTLS 3.5.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}gnutls-server:3.5.${array[$i]} -f Dockerfile-3_5_8-16 --target gnutls-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}gnutls-client:3.5.${array[$i]} -f Dockerfile-3_5_8-16 --target gnutls-client .
	i=i+1
done


array=(0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: GnuTLS 3.4.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}gnutls-server:3.4.${array[$i]} -f Dockerfile-3_4_x --target gnutls-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}gnutls-client:3.4.${array[$i]} -f Dockerfile-3_4_x --target gnutls-client .
	i=i+1
done


# 3.3.1 does not compile due to strange errors
array=(0 2 3 4 5 6 8 9 10 11 12 13 14 15)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: GnuTLS 3.3.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}gnutls-server:3.3.${array[$i]} -f Dockerfile-3_3_0-15 --target gnutls-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}gnutls-client:3.3.${array[$i]} -f Dockerfile-3_3_0-15 --target gnutls-client .
	i=i+1
done


array=(16 17 18 19 20 21 22 23 24 25 26 27 28 29 30)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: GnuTLS 3.3.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}gnutls-server:3.3.${array[$i]} -f Dockerfile-3_3_x --target gnutls-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}gnutls-client:3.3.${array[$i]} -f Dockerfile-3_3_x --target gnutls-client .
	i=i+1
done

exit "$EXITCODE"
