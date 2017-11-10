#!/bin/bash
array=(34 33 32 31 30 24 23 22 21 20 19 17 16 15 14 13 12 11 10 9 8)
# 0, 1, 2, 7 and 18 not supported
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Feld $i: Botan 1.11.${array[$i]}"
	docker build --build-arg VERSION=${array[$i]} -t botan-1_11_${array[$i]}-server -f Dockerfile-1_11_x .
	i=i+1
done



docker build --build-arg VERSION=29 -t botan-1_11_29-server -f Dockerfile-1_11_25-29 .
docker build --build-arg VERSION=28 -t botan-1_11_28-server -f Dockerfile-1_11_25-29 .
docker build --build-arg VERSION=27 -t botan-1_11_27-server -f Dockerfile-1_11_25-29 .
docker build --build-arg VERSION=26 -t botan-1_11_26-server -f Dockerfile-1_11_25-29 .
docker build --build-arg VERSION=25 -t botan-1_11_25-server -f Dockerfile-1_11_25-29 .

docker build --build-arg VERSION=13 -t botan-1_11_13-server -f Dockerfile-1_11_8-13 .
docker build --build-arg VERSION=12 -t botan-1_11_12-server -f Dockerfile-1_11_8-13 .
docker build --build-arg VERSION=11 -t botan-1_11_11-server -f Dockerfile-1_11_8-13 .
docker build --build-arg VERSION=10 -t botan-1_11_10-server -f Dockerfile-1_11_8-13 .
docker build --build-arg VERSION=9 -t botan-1_11_9-server -f Dockerfile-1_11_8-13 .
docker build --build-arg VERSION=8 -t botan-1_11_8-server -f Dockerfile-1_11_8-13 .

docker build --build-arg VERSION=3 -t botan-1_11_3-server -f Dockerfile-1_11_3-6 .
docker build --build-arg VERSION=4 -t botan-1_11_4-server -f Dockerfile-1_11_3-6 .
docker build --build-arg VERSION=5 -t botan-1_11_5-server -f Dockerfile-1_11_3-6 .
docker build --build-arg VERSION=6 -t botan-1_11_6-server -f Dockerfile-1_11_3-6 .
