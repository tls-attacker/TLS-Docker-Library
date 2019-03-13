#!/bin/bash

array=(0.13.0)
typeset -i i=0 max=${#array[*]}

while (( i < max ))
do
	echo "Feld $i: python-mbedtls-${array[$i]}"
	docker build --build-arg VERSION=${array[$i]} -t python-mbedtls-${array[$i]}-server -f Dockerfile .
	i=i+1
done
