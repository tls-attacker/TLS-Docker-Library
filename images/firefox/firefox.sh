#!/bin/bash

echo "Building Firefox Images"

#Normal Versions
array=(61.0.2 60.0.2 59.0.3 58.0.2 57.0.4 56.0.2 55.0.3 54.0.1 53.0.3 52.0.2 51.0.1 50.0.2)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: Firefox ${array[$i]}"
	docker build --build-arg VERSION=${array[$i]} -t firefox-${array[$i]}-client .
	i=i+1
done

#ESR Versions
array=(60.1.0esr 52.9.0esr 45.9.0esr 38.8.0esr 31.8.0esr 24.8.1esr 17.0.11esr 10.0.12esr)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: Firefox ${array[$i]}"
	docker build --build-arg VERSION=${array[$i]} -t firefox-${array[$i]}-client .
	i=i+1
done
