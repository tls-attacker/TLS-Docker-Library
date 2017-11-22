#!/bin/bash
array=(9-3 9-1 9-0 8-7b 8-7a 8-7 8-6 8-4 8-3)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: Matrixssl 3-${array[$i]}"
	docker build --build-arg VERSION=${array[$i]} -t martixssl-3-${array[$i]}-server -f Dockerfile-3_x .
	i=i+1
done

docker build -t martixssl-3-8-3-server -f Dockerfile-3_8_3 .
array=(7.2 4.0)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: Matrixssl 3-${array[$i]}"
	docker build --build-arg VERSION=${array[$i]} -t martixssl-3-${array[$i]}-server -f Dockerfile-3_7_2_and_3_4_0 .
	i=i+1
done


