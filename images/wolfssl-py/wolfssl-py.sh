#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

array=(v3.13.0-1 v3.13.0-0 v3.12.2)
typeset -i i=0 max=${#array[*]}

while (( i < max ))
do
	echo "Feld $i: wolfssl-py-${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t wolfssl-py-${array[$i]}-server -f Dockerfile .
	i=i+1
done

exit "$EXITCODE"
