#!/bin/bash

array=(18.0.0 17.5.0 17.4.0 17.3.0 17.2.0 17.1.0 17.0.0 16.2.0 16.1.0 16.0.0 0.15.1)
typeset -i i=0 max=${#array[*]}

while (( i < max ))
do
	echo "Feld $i: py-openssl-${array[$i]}"
	docker build --build-arg VERSION=${array[$i]} -t py-openssl-${array[$i]}-server -f Dockerfile .
	i=i+1
done
