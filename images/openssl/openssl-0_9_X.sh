#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

#array6=(i j k l m)
#typeset -i i=0 max=${#array6[*]}
#while (( i < max ))
#do
#	echo "Feld $i: Openssl 0.9.6${array6[$i]}"
#	_docker build --build-arg VERSION=6${array6[$i]} -t openssl-0_9_6${array6[$i]}-server -f Dockerfile-0_9_6x --target openssl-server .
#	_docker build --build-arg VERSION=6${array6[$i]} -t openssl-0_9_6${array6[$i]}-client -f Dockerfile-0_9_6x --target openssl-client .
#	i=i+1
#	read -rsp $'Press enter to continue...\n'
#done
array7=(a b c d e f g h i j k l m)
# version 0.9.7x
_docker build --build-arg VERSION=7 -t openssl-0_9_7-server -f Dockerfile-0_9_7x --target openssl-server .
_docker build --build-arg VERSION=7 -t openssl-0_9_7-client -f Dockerfile-0_9_7x --target openssl-client .
typeset -i i=0 max=${#array7[*]}
while (( i < max ))
do
	echo "Feld $i: Openssl 0.9.7${array7[$i]}"
	_docker build --build-arg VERSION=7${array7[$i]} -t openssl-0_9_7${array7[$i]}-server -f Dockerfile-0_9_7x --target openssl-server .
	_docker build --build-arg VERSION=7${array7[$i]} -t openssl-0_9_7${array7[$i]}-client -f Dockerfile-0_9_7x --target openssl-client .
	i=i+1
done

array8=(a b c d e f g h i j k l m n o p q r s t u v w x y za zb zc zd ze zf zg zh)
# version 0.9.8x
_docker build --build-arg VERSION=8 -t openssl-0_9_8-server -f Dockerfile-0_9_8a-n --target openssl-server .
_docker build --build-arg VERSION=8 -t openssl-0_9_8-client -f Dockerfile-0_9_8a-n --target openssl-client .
typeset -i i=0 max=${#array8[*]}
while (( i < 12 ))
do
	echo "Feld $i: Openssl 0.9.8${array8[$i]}"
	_docker build --build-arg VERSION=8${array8[$i]} -t openssl-0_9_8${array8[$i]}-server -f Dockerfile-0_9_8a-n --target openssl-server .
	_docker build --build-arg VERSION=8${array8[$i]} -t openssl-0_9_8${array8[$i]}-client -f Dockerfile-0_9_8a-n --target openssl-client .
	i=i+1
done

while (( i < max))
do
	echo "Feld $i: Openssl 0.9.8${array8[$i]}"
	_docker build --build-arg VERSION=8${array8[$i]} -t openssl-0_9_8${array8[$i]}-server -f Dockerfile-0_9_8m-zh --target openssl-server .
	_docker build --build-arg VERSION=8${array8[$i]} -t openssl-0_9_8${array8[$i]}-client -f Dockerfile-0_9_8m-zh --target openssl-client .
	i=i+1
done
echo "Feld $i: Openssl 0.9.8m-beta1"
_docker build --build-arg VERSION=8m-beta1 -t openssl-0_9_8m-beta1-server -f Dockerfile-0_9_8m-zh --target openssl-server .
_docker build --build-arg VERSION=8m-beta1 -t openssl-0_9_8m-beta1-client -f Dockerfile-0_9_8m-zh --target openssl-client .

exit "$EXITCODE"
