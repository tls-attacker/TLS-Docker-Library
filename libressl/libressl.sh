#!/bin/bash
array=(0 1 2 3)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: LibreSSL 2.6.${array[$i]}"
	docker build --build-arg VERSION=6.${array[$i]} -t libressl-2_6_${array[$i]}-server -f Dockerfile-2_x .
	i=i+1
done

array=(0 1 2 3 4 5)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: LibreSSL 2.5.${array[$i]}"
	docker build --build-arg VERSION=5.${array[$i]} -t libressl-2_5_${array[$i]}-server -f Dockerfile-2_x .
	i=i+1
done

array=(0 1 2 3 4 5)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: LibreSSL 2.4.${array[$i]}"
	docker build --build-arg VERSION=4.${array[$i]} -t libressl-2_4_${array[$i]}-server -f Dockerfile-2_x .
	i=i+1
done

array=(0 1 2 3 4 5 6 7 8 9 10)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: LibreSSL 2.3.${array[$i]}"
	docker build --build-arg VERSION=3.${array[$i]} -t libressl-2_3_${array[$i]}-server -f Dockerfile-2_x .
	i=i+1
done

array=(0 1 2 3 4 5 6 7 8 9)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: LibreSSL 2.2.${array[$i]}"
	docker build --build-arg VERSION=2.${array[$i]} -t libressl-2_2_${array[$i]}-server -f Dockerfile-2_x .
	i=i+1
done

array=(0 1 2 3 4 5 6 7 8 9 10)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: LibreSSL 2.1.${array[$i]}"
	docker build --build-arg VERSION=1.${array[$i]} -t libressl-2_1_${array[$i]}-server -f Dockerfile-2_x .
	i=i+1
done

array=(0 1 2 3 4 5 6)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: LibreSSL 2.0.${array[$i]}"
	docker build --build-arg VERSION=0.${array[$i]} -t libressl-2_0_${array[$i]}-server -f Dockerfile-2_x .
	i=i+1
done
