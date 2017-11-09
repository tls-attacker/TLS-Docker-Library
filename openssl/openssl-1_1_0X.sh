#!/bin/bash

array=(a b c d e f -pre3)
typeset -i i=0 max=${#array[*]}

while (( i < max ))
do
	echo "Feld $i: Openssl 1.1.0${array[$i]}"
	docker build --build-arg VERSION=${array[$i]} -t openssl-1_1_0${array[$i]}-server -f Dockerfile-1_1_0x .
	i=i+1
done
