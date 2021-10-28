#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

#array6=(i j k l m)
#typeset -i i=0 max=${#array6[*]}
#while (( i < max ))
#do
#	echo "Feld $i: Openssl 0.9.6${array6[$i]}"
#	_docker build --build-arg VERSION=6${array6[$i]} -t ${DOCKER_REPOSITORY}openssl-server:0.9.6${array6[$i]} -f Dockerfile-0_9_6x --target openssl-server .
#	_docker build --build-arg VERSION=6${array6[$i]} -t ${DOCKER_REPOSITORY}openssl-client:0.9.6${array6[$i]} -f Dockerfile-0_9_6x --target openssl-client .
#	i=i+1
#	read -rsp $'Press enter to continue...\n'
#done
array7=(a b c d e f g h i j k l m)
# version 0.9.7x
_docker build --build-arg VERSION=7 -t ${DOCKER_REPOSITORY}openssl-server:0.9.7 -f Dockerfile-0_9_7x --target openssl-server .
_docker build --build-arg VERSION=7 -t ${DOCKER_REPOSITORY}openssl-client:0.9.7 -f Dockerfile-0_9_7x --target openssl-client .
if [ ! -z "$DOCKER_REPOSITORY" ]; then
	_docker push ${DOCKER_REPOSITORY}openssl-server:0.9.7
	_docker push ${DOCKER_REPOSITORY}openssl-client:0.9.7
fi
typeset -i i=0 max=${#array7[*]}
while (( i < max ))
do
	echo "Feld $i: Openssl 0.9.7${array7[$i]}"
	_docker build --build-arg VERSION=7${array7[$i]} -t ${DOCKER_REPOSITORY}openssl-server:0.9.7${array7[$i]} -f Dockerfile-0_9_7x --target openssl-server .
	_docker build --build-arg VERSION=7${array7[$i]} -t ${DOCKER_REPOSITORY}openssl-client:0.9.7${array7[$i]} -f Dockerfile-0_9_7x --target openssl-client .
	if [ ! -z "$DOCKER_REPOSITORY" ]; then
		_docker push ${DOCKER_REPOSITORY}openssl-server:0.9.7${array7[$i]}
		_docker push ${DOCKER_REPOSITORY}openssl-client:0.9.7${array7[$i]}
	fi
	i=i+1
done

array8=(a b c d e f g h i j k l m n o p q r s t u v w x y za zb zc zd ze zf zg zh)
# version 0.9.8x
_docker build --build-arg VERSION=8 -t ${DOCKER_REPOSITORY}openssl-server:0.9.8 -f Dockerfile-0_9_8a-n --target openssl-server .
_docker build --build-arg VERSION=8 -t ${DOCKER_REPOSITORY}openssl-client:0.9.8 -f Dockerfile-0_9_8a-n --target openssl-client .
typeset -i i=0 max=${#array8[*]}
while (( i < 12 ))
do
	echo "Feld $i: Openssl 0.9.8${array8[$i]}"
	_docker build --build-arg VERSION=8${array8[$i]} -t ${DOCKER_REPOSITORY}openssl-server:0.9.8${array8[$i]} -f Dockerfile-0_9_8a-n --target openssl-server .
	_docker build --build-arg VERSION=8${array8[$i]} -t ${DOCKER_REPOSITORY}openssl-client:0.9.8${array8[$i]} -f Dockerfile-0_9_8a-n --target openssl-client .
	if [ ! -z "$DOCKER_REPOSITORY" ]; then
		_docker push ${DOCKER_REPOSITORY}openssl-server:0.9.8${array8[$i]}
		_docker push ${DOCKER_REPOSITORY}openssl-client:0.9.8${array8[$i]}
	fi
	i=i+1
done

while (( i < max))
do
	echo "Feld $i: Openssl 0.9.8${array8[$i]}"
	_docker build --build-arg VERSION=8${array8[$i]} -t ${DOCKER_REPOSITORY}openssl-server:0.9.8${array8[$i]} -f Dockerfile-0_9_8m-zh --target openssl-server .
	_docker build --build-arg VERSION=8${array8[$i]} -t ${DOCKER_REPOSITORY}openssl-client:0.9.8${array8[$i]} -f Dockerfile-0_9_8m-zh --target openssl-client .
	if [ ! -z "$DOCKER_REPOSITORY" ]; then
		_docker push ${DOCKER_REPOSITORY}openssl-server:0.9.8${array8[$i]}
		_docker push ${DOCKER_REPOSITORY}openssl-client:0.9.8${array8[$i]}
	fi
	i=i+1
done
echo "Feld $i: Openssl 0.9.8m-beta1"
_docker build --build-arg VERSION=8m-beta1 -t ${DOCKER_REPOSITORY}openssl-server:0.9.8m_beta1 -f Dockerfile-0_9_8m-zh --target openssl-server .
_docker build --build-arg VERSION=8m-beta1 -t ${DOCKER_REPOSITORY}openssl-client:0.9.8m_beta1 -f Dockerfile-0_9_8m-zh --target openssl-client .
	if [ ! -z "$DOCKER_REPOSITORY" ]; then
		_docker push ${DOCKER_REPOSITORY}openssl-server:0.9.8m_beta1
		_docker push ${DOCKER_REPOSITORY}openssl-client:0.9.8m_beta1
	fi

exit "$EXITCODE"
