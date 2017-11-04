#!/bin/sh

#array6=(i j k l m)
#typeset -i i=0 max=${#array6[*]}
#while (( i < max ))
#do
#	echo "Feld $i: Openssl 0.9.6${array6[$i]}"
#	docker build --build-arg VERSION=6${array6[$i]} -t openssl-0_9_6${array6[$i]}-server -f Dockerfile-0_9_6x .
#	i=i+1
#	read -rsp $'Press enter to continue...\n'
#done
array7=(a b c d e f g h i j k l m)
# version 0.9.7x
docker build --build-arg VERSION=7 -t openssl-0_9_7-server -f Dockerfile-0_9_7 .
typeset -i i=0 max=${#array7[*]}
while (( i < max ))
do
	echo "Feld $i: Openssl 0.9.7${array7[$i]}"
	docker build --build-arg VERSION=7${array7[$i]} -t openssl-0_9_7${array7[$i]}-server -f Dockerfile-0_9_7x .
	i=i+1
done

array8=(a b c d e f g h i j k l m n o p q r s t u v w x y za zb zc zd ze zf zg zh)
# version 0.9.8x
docker build --build-arg VERSION=8 -t openssl-0_9_8-server -f Dockerfile-0_9_8a-n .
typeset -i i=0 max=${#array8[*]}
while (( i < 12 ))
do
	echo "Feld $i: Openssl 0.9.8${array8[$i]}"
	docker build --build-arg VERSION=8${array8[$i]} -t openssl-0_9_8${array8[$i]}-server -f Dockerfile-0_9_8a-n .
	i=i+1
done

while (( i < max))
do
	echo "Feld $i: Openssl 0.9.8${array8[$i]}"
	docker build --build-arg VERSION=8${array8[$i]} -t openssl-0_9_8${array8[$i]}-server -f Dockerfile-0_9_8m-zh .
	i=i+1
done
echo "Feld $i: Openssl 0.9.8m-beta1"
docker build --build-arg VERSION=8m-beta1 -t openssl-0_9_8m-beta1-server -f Dockerfile-0_9_8m-zh .
