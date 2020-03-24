#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

array=(a b c d e -pre1 -pre2 -pre3 -pre4 -pre5 -pre6 -pre7 -pre8 -pre9)
typeset -i i=0 max=${#array[*]}
_docker build --build-arg VERSION= -t openssl-1_1_1-server -f Dockerfile-1_1_1x --target openssl-server .
_docker build --build-arg VERSION= -t openssl-1_1_1-client -f Dockerfile-1_1_1x --target openssl-client .
while (( i < max ))
do
	echo "Feld $i: Openssl 1.1.1${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t openssl-1_1_1${array[$i]}-server -f Dockerfile-1_1_1x --target openssl-server .
	_docker build --build-arg VERSION=${array[$i]} -t openssl-1_1_1${array[$i]}-client -f Dockerfile-1_1_1x --target openssl-client .
	i=i+1
done

exit "$EXITCODE"
