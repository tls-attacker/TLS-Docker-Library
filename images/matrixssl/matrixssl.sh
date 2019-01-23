#!/bin/bash
array=(9-3 9-1 9-0 8-7b 8-7a 8-7 8-6 8-4 8-3)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: Matrixssl 3-${array[$i]}"
	docker build --build-arg VERSION=${array[$i]} -t matrixssl-3-${array[$i]}-server -f Dockerfile-3_x --target matrixssl-server .
	docker build --build-arg VERSION=${array[$i]} -t matrixssl-3-${array[$i]}-client -f Dockerfile-3_x --target matrixssl-client .
	i=i+1
done

array=(7.2 4.0)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: Matrixssl 3-${array[$i]}"
	docker build --build-arg VERSION=${array[$i]} -t matrixssl-3-${array[$i]}-server -f Dockerfile-3_7_2_and_3_4_0 --target matrixssl-server .
	docker build --build-arg VERSION=${array[$i]} -t matrixssl-3-${array[$i]}-client -f Dockerfile-3_7_2_and_3_4_0 --target matrixssl-client .
	i=i+1
done


