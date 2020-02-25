#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

array=(a b c d e f g h i -pre3)
typeset -i i=0 max=${#array[*]}
_docker build --build-arg VERSION= -t openssl-1_1_0-server -f Dockerfile-1_1_0x --target openssl-server .
_docker build --build-arg VERSION= -t openssl-1_1_0-client -f Dockerfile-1_1_0x --target openssl-client .
while (( i < max ))
do
	echo "Feld $i: Openssl 1.1.0${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t openssl-1_1_0${array[$i]}-server -f Dockerfile-1_1_0x --target openssl-server .
	_docker build --build-arg VERSION=${array[$i]} -t openssl-1_1_0${array[$i]}-client -f Dockerfile-1_1_0x --target openssl-client .
	i=i+1
done

exit "$EXITCODE"
