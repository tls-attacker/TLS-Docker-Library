#!/bin/bash
array=(4 5 6 7 8)
typeset -i i=0 max=${#array[*]}

while (( i < max ))
do
	echo "Feld $i: Botan 2.${array[$i]}.0"
	docker build --build-arg VERSION=${array[$i]} -t botan-2_${array[$i]}_0-server -f Dockerfile-2_X_0 .
	i=i+1
done

