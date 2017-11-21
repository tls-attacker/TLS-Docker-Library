#!/bin/bash
array=(50 51 52 53 54 55 56 57 58)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: BouncyCastleTLS 1.${array[$i]}"
	docker build --build-arg VERSION=${array[$i]} -t bouncycastletls-1-${array[$i]}-server -f Dockerfile-1_x .
	i=i+1
done