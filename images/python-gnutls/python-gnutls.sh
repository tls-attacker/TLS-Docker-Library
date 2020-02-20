#!/bin/bash
#Builds Container with Compilerenv. !

array=(release-3.1.2)
typeset -i i=0 max=${#array[*]}

while (( i < max ))
do
	echo "Feld $i: python-gnutls-${array[$i]}"
	docker build --build-arg VERSION=${array[$i]} -t python-gnutls-${array[$i]}-server -f Dockerfile .
	i=i+1
done
