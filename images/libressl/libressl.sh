#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh


# version 3.3.x
array=(0 1)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
        echo "Building: LibreSSL 3.2.${array[$i]}"
        _docker build --build-arg VERSION=3.3.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-server:3.3.${array[$i]} -f Dockerfile-2_x --target libressl-server .
        _docker build --build-arg VERSION=3.3.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-client:3.3.${array[$i]} -f Dockerfile-2_x --target libressl-client .
        i=i+1
done

# version 3.2.x
array=(0 1 2 3)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
        echo "Building: LibreSSL 3.2.${array[$i]}"
        _docker build --build-arg VERSION=3.2.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-server:3.2.${array[$i]} -f Dockerfile-2_x --target libressl-server .
        _docker build --build-arg VERSION=3.2.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-client:3.2.${array[$i]} -f Dockerfile-2_x --target libressl-client .
        i=i+1
done

# version 3.1.x
array=(0 1 2 3 4 5)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
        echo "Building: LibreSSL 3.1.${array[$i]}"
        _docker build --build-arg VERSION=3.1.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-server:3.1.${array[$i]} -f Dockerfile-2_x --target libressl-server .
        _docker build --build-arg VERSION=3.1.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-client:3.1.${array[$i]} -f Dockerfile-2_x --target libressl-client .
        i=i+1
done

# version 3.0.x
array=(0 1 2)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
        echo "Building: LibreSSL 3.0.${array[$i]}"
        _docker build --build-arg VERSION=3.0.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-server:3.0.${array[$i]} -f Dockerfile-2_x --target libressl-server .
        _docker build --build-arg VERSION=3.0.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-client:3.0.${array[$i]} -f Dockerfile-2_x --target libressl-client .
        i=i+1
done

# version 2.9.x
# 2.9.2 does not compile
#   compat/getprogname_linux.c: In function 'getprogname':
#   compat/getprogname_linux.c:32:2: error: #error "Cannot emulate getprogname"
array=(0 1)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
        echo "Building: LibreSSL 2.9.${array[$i]}"
        _docker build --build-arg VERSION=2.9.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-server:2.9.${array[$i]} -f Dockerfile-2_x --target libressl-server .
        _docker build --build-arg VERSION=2.9.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-client:2.9.${array[$i]} -f Dockerfile-2_x --target libressl-client .
        i=i+1
done


#version 2.8.x
array=(0 1 2 3)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
        echo "Building: LibreSSL 2.8.${array[$i]}"
        _docker build --build-arg VERSION=2.8.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-server:2.8.${array[$i]} -f Dockerfile-2_x --target libressl-server .
        _docker build --build-arg VERSION=2.8.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-client:2.8.${array[$i]} -f Dockerfile-2_x --target libressl-client .
        i=i+1
done

# version 2.7.x
array=(0 1 2 3 4 5)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
        echo "Building: LibreSSL 2.7.${array[$i]}"
        _docker build --build-arg VERSION=2.7.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-server:2.7.${array[$i]} -f Dockerfile-2_x --target libressl-server .
        _docker build --build-arg VERSION=2.7.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-client:2.7.${array[$i]} -f Dockerfile-2_x --target libressl-client .
        i=i+1
done

# version 2.6.x
array=(0 1 2 3 4 5)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: LibreSSL 2.6.${array[$i]}"
	_docker build --build-arg VERSION=2.6.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-server:2.6.${array[$i]} -f Dockerfile-2_x --target libressl-server .
	_docker build --build-arg VERSION=2.6.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-client:2.6.${array[$i]} -f Dockerfile-2_x --target libressl-client .
	i=i+1
done

# version 2.5.x
array=(0 1 2 3 4 5)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: LibreSSL 2.5.${array[$i]}"
	_docker build --build-arg VERSION=2.5.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-server:2.5.${array[$i]} -f Dockerfile-2_x --target libressl-server .
	_docker build --build-arg VERSION=2.5.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-client:2.5.${array[$i]} -f Dockerfile-2_x --target libressl-client .
	i=i+1
done

# version 2.4.x
array=(0 1 2 3 4 5)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: LibreSSL 2.4.${array[$i]}"
	_docker build --build-arg VERSION=2.4.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-server:2.4.${array[$i]} -f Dockerfile-2_x --target libressl-server .
	_docker build --build-arg VERSION=2.4.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-client:2.4.${array[$i]} -f Dockerfile-2_x --target libressl-client .
	i=i+1
done

# version 2.3.x
# 2.3.0 does not compile
#   undefined reference to `b64_ntop'
array=(1 2 3 4 5 6 7 8 9 10)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: LibreSSL 2.3.${array[$i]}"
	_docker build --build-arg VERSION=2.3.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-server:2.3.${array[$i]} -f Dockerfile-2_x --target libressl-server .
	_docker build --build-arg VERSION=2.3.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-client:2.3.${array[$i]} -f Dockerfile-2_x --target libressl-client .
	i=i+1
done

# version 2.2.x
# 2.2.0 does not compile
#   error: sys/sysctl.h: No such file or directory
array=(1 2 3 4 5 6 7 8 9)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: LibreSSL 2.2.${array[$i]}"
	_docker build --build-arg VERSION=2.2.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-server:2.2.${array[$i]} -f Dockerfile-2_x --target libressl-server .
	_docker build --build-arg VERSION=2.2.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-client:2.2.${array[$i]} -f Dockerfile-2_x --target libressl-client .
	i=i+1
done

# version 2.1.x
array=(0 1 2 3 4 5 6 7 8 9 10)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: LibreSSL 2.1.${array[$i]}"
	_docker build --build-arg VERSION=2.1.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-server:2.1.${array[$i]} -f Dockerfile-2_x --target libressl-server .
	_docker build --build-arg VERSION=2.1.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-client:2.1.${array[$i]} -f Dockerfile-2_x --target libressl-client .
	i=i+1
done

# version 2.0.x
# 2.0.0 does not compile
array=(1 2 3 4 5 6)
typeset -i i=0 max=${#array[*]}
while (( i < max ))
do
	echo "Building: LibreSSL 2.0.${array[$i]}"
	_docker build --build-arg VERSION=2.0.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-server:2.0.${array[$i]} -f Dockerfile-2_x --target libressl-server .
	_docker build --build-arg VERSION=2.0.${array[$i]} -t ${DOCKER_REPOSITORY}libressl-client:2.0.${array[$i]} -f Dockerfile-2_x --target libressl-client .
	i=i+1
done

exit "$EXITCODE"
