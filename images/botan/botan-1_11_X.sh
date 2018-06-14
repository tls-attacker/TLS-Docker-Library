#!/bin/bash
# Script for building botan version 1.11.X docker images
# 0, 1, 2, 7 and 18 not supported
array=(3 4 5 6)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: Botan 1.11.${array[$i]}"
	docker build --build-arg VERSION=${array[$i]} -t botan-1_11_${array[$i]}-server -f Dockerfile-1_11_3-6 --target botan-server .
	docker build --build-arg VERSION=${array[$i]} -t botan-1_11_${array[$i]}-client -f Dockerfile-1_11_3-6 --target botan-client .
	i=i+1
done

array=(8 9 10 11 12 13)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: Botan 1.11.${array[$i]}"
	docker build --build-arg VERSION=${array[$i]} -t botan-1_11_${array[$i]}-server -f Dockerfile-1_11_8-13 --target botan-server .
	docker build --build-arg VERSION=${array[$i]} -t botan-1_11_${array[$i]}-client -f Dockerfile-1_11_8-13 --target botan-client .
	i=i+1
done

#Build error in test with v33 -> check that
array=(14 15 16 17 19 20 21 22 23 24 30 31 32 33)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: Botan 1.11.${array[$i]}"
	#docker build --build-arg VERSION=${array[$i]} -t botan-1_11_${array[$i]}-server -f Dockerfile-1_11_x --target botan-server .
	#docker build --build-arg VERSION=${array[$i]} -t botan-1_11_${array[$i]}-client -f Dockerfile-1_11_x --target botan-client .
	i=i+1
done

docker build --build-arg VERSION=34 -t botan-1_11_34-server -f Dockerfile-1_11_34 --target botan-server .
	docker build --build-arg VERSION=34 -t botan-1_11_34-client -f Dockerfile-1_11_34 --target botan-client .

array=(25 26 27 28 29)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: Botan 1.11.${array[$i]}"
	docker build --build-arg VERSION=${array[$i]} -t botan-1_11_${array[$i]}-server -f Dockerfile-1_11_25-29 --target botan-server .
	docker build --build-arg VERSION=${array[$i]} -t botan-1_11_${array[$i]}-client -f Dockerfile-1_11_25-29 --target botan-client .
	i=i+1
done
