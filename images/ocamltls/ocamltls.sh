#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

array=(0.12.8 0.10.5)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Build OCaml-TLS ${array[$i]}:"
	_docker build --build-arg VERSION=v${array[$i]} -t ${DOCKER_REPOSITORY}ocamltls-server:${array[$i]} -f Dockerfile --target ocamltls-server .
	_docker build --build-arg VERSION=v${array[$i]} -t ${DOCKER_REPOSITORY}ocamltls-client:${array[$i]} -f Dockerfile --target ocamltls-client .
	i=i+1
done

array=(0.10.4 0.8.0)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Build OCaml-TLS ${array[$i]}:"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}ocamltls-server:${array[$i]} -f Dockerfile --target ocamltls-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}ocamltls-client:${array[$i]} -f Dockerfile --target ocamltls-client .
	i=i+1
done

exit "$EXITCODE"
