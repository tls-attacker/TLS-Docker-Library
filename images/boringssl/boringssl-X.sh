#!/bin/bash
# Script for building boringssl versions
# >2214 supports servermode
# shim is not working for 2272 2311 2357
array=(2272 2311 2357)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: BoringSSL ${array[$i]}"
	docker build --build-arg VERSION=${array[$i]} -t boringssl-${array[$i]}-server -f Dockerfile-2272-2357 .
	i=i+1
done

array=(2490 2564 2623 2661 3239)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: BoringSSL ${array[$i]}"
	docker build --build-arg VERSION=${array[$i]} -t boringssl-${array[$i]}-server -f Dockerfile-2214-2661 .
	i=i+1
done

array=(2704 2883 2924 2987 3029 3112 3202 chromium-stable master)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: BoringSSL ${array[$i]}"
	docker build --build-arg VERSION=${array[$i]} -t boringssl-${array[$i]}-server -f Dockerfile-x .
	i=i+1
done
