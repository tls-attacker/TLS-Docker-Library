#!/bin/bash
array=(-pre2 -pre3 -pre4 -pre5 -pre6 -pre7 -pre8 -pre9)
typeset -i i=0 max=${#array[*]}

docker build --build-arg VERSION= -t openssl-1_1_1 -f Dockerfile-1_1_1x .

while (( i < max ))
do
	echo "Feld $i: Openssl 1.1.1${array[$i]}"
	docker build --build-arg VERSION=${array[$i]} -t openssl-1_1_1${array[$i]}-server -f Dockerfile-1_1_1x .
	i=i+1
done

