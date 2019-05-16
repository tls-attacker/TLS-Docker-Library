#!/bin/bash

array=(a b c d e f g h i j k l m n o p)
typeset -i i=0 max=${#array[*]}
docker build --build-arg VERSION= -t openssl-1_0_2-server -f Dockerfile-1_0_2_and_betas --target openssl-server .
docker build --build-arg VERSION= -t openssl-1_0_2-client -f Dockerfile-1_0_2_and_betas --target openssl-client .

docker build --build-arg VERSION=-beta1 -t openssl-1_0_2-beta1-server -f Dockerfile-1_0_2_and_betas --target openssl-server .
docker build --build-arg VERSION=-beta1 -t openssl-1_0_2-beta1-client -f Dockerfile-1_0_2_and_betas --target openssl-client .

docker build --build-arg VERSION=-beta2 -t openssl-1_0_2-beta2-server -f Dockerfile-1_0_2_and_betas --target openssl-server .
docker build --build-arg VERSION=-beta2 -t openssl-1_0_2-beta2-client -f Dockerfile-1_0_2_and_betas --target openssl-client .

docker build --build-arg VERSION=-beta3 -t openssl-1_0_2-beta3-server -f Dockerfile-1_0_2_and_betas --target openssl-server .
docker build --build-arg VERSION=-beta3 -t openssl-1_0_2-beta3-client -f Dockerfile-1_0_2_and_betas --target openssl-client .

while (( i < max ))
do
	echo "Feld $i: Openssl 1.0.2${array[$i]}"
	docker build --build-arg VERSION=${array[$i]} -t openssl-1_0_2${array[$i]}-server -f Dockerfile-1_0_2x --target openssl-server .
	docker build --build-arg VERSION=${array[$i]} -t openssl-1_0_2${array[$i]}-client -f Dockerfile-1_0_2x --target openssl-client .
	i=i+1
done
