#!/bin/bash

array=(a b c d e f g h i j k l -beta1 -beta2 -beta3 m n o p q r s t u)
typeset -i i=0 max=${#array[*]}
docker build --build-arg VERSION= -t openssl-1_0_1-server -f Dockerfile-1_0_1x .
while (( i < 15 ))
do
	echo "Feld $i: Openssl 1.0.1${array[$i]}"
	docker build --build-arg VERSION=${array[$i]} -t openssl-1_0_1${array[$i]}-server -f Dockerfile-1_0_1x .
	i=i+1
done

while (( i < max ))
do
	echo "Feld $i: Openssl 1.0.1${array[$i]}"
	docker build --build-arg VERSION=${array[$i]} -t openssl-1_0_1${array[$i]}-server -f Dockerfile-1_0_1m-u .
	i=i+1
done