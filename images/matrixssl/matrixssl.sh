#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

array=(6.0 5.1 3.0 2.2 2.1 1.0 0.2 0.1 0.0)
commits=(effeb14219ab9b9560ddf0ea56f939a1aa8f1d71 69b5f2c6c335ebc18a131822660bbfa536dcf5d2 eec42aa814dca8285457cc808a150347b3ac8c00 f0b0d0a5c39065fe9f02e011a0f01e406054a387 91fd0f130294629fe6f17078c5306af42ca6cfa5 dbc2786c73d32f98c6496176c356778b6dac7de6 1518527031e43e5a6fda9f3b39d474041526a55a 5a72845b65d0b39bd2ac0921b3b38b60e34a7128 d0a51a7e434019ff20ba3dfb47fb9f9bafbb3540)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: Matrixssl 4.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} --build-arg COMMIT=${commits[$i]} -t ${DOCKER_REPOSITORY}matrixssl-server:4.${array[$i]} -f Dockerfile-4_x --target matrixssl-server .
	_docker build --build-arg VERSION=${array[$i]} --build-arg COMMIT=${commits[$i]} -t ${DOCKER_REPOSITORY}matrixssl-client:4.${array[$i]} -f Dockerfile-4_x --target matrixssl-client .
	i=i+1
done

# 3.8.3 needs certificates in a specific place
#   if this version is needed, create a specific dockerfile for it
array=(9.5 9.3 9.1 9.0 8.6 8.4)
commits=(83bff65b84d6f76b69d44a115469da2dd68decae 0790908cb0dd035f6dfe5bb27ecddb9af1596f5e e05dfbf65050c13ae02ca41cd6cae575e5403a28 7c741e9005644ca19c723c86311a14ea207af4dd aa9fb8e32c2b433aaf3fde67e61179f5eeb0a132 dba6bac4a25b94362c7d0d9e603db2bef178ba96 866749ebd8406f5956fc2ce9976337eb411bb592)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: Matrixssl 3.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} --build-arg COMMIT=${commits[$i]}  -t ${DOCKER_REPOSITORY}matrixssl-server:3.${array[$i]} -f Dockerfile-3_x --target matrixssl-server .
	_docker build --build-arg VERSION=${array[$i]} --build-arg COMMIT=${commits[$i]}  -t ${DOCKER_REPOSITORY}matrixssl-client:3.${array[$i]} -f Dockerfile-3_x --target matrixssl-client .
	i=i+1
done

array=(7.2 4.0)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: Matrixssl 3.${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}matrixssl-server:3.${array[$i]} -f Dockerfile-3_7_2_and_3_4_0 --target matrixssl-server .
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}matrixssl-client:3.${array[$i]} -f Dockerfile-3_7_2_and_3_4_0 --target matrixssl-client .
	i=i+1
done



exit "$EXITCODE"
