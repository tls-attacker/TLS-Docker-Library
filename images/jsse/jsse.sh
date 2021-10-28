#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

bcversion=59
jreversions=(7 7u151 8 8u162 9.0 9.0.4-12)
typeset -i i=0 max=${#jreversions[*]}
while (( i < max ))
do
	echo "Building: JsseTLS 1.${jreversions[$i]}"
	_docker build --build-arg JRE_VERSION=openjdk:${jreversions[$i]}-jre-slim --build-arg BC_VERSION=${bcversion} -t ${DOCKER_REPOSITORY}jssetls_jre-server:${jreversions[$i]}_bc_1_${bcversion} -f Dockerfile .
	i=i+1
done

bcversion=50
jreversion=7u151
_docker build --build-arg JRE_VERSION=openjdk:${jreversion}-jre-slim --build-arg BC_VERSION=${bcversion} -t ${DOCKER_REPOSITORY}jssetls_jre-server:${jreversion}_bc_1_${bcversion} -f Dockerfile .

exit "$EXITCODE"
