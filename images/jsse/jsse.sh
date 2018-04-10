#!/bin/bash
bcversion=59
jreversions=(7 7u151 8 8u162 9.0 9.0.4-12)
typeset -i i=0 max=${#jreversions[*]}
while (( i < max ))
do
	echo "Building: JsseTLS 1.${jreversions[$i]}"
	docker build --build-arg JRE_VERSION=openjdk:${jreversions[$i]}-jre-slim --build-arg BC_VERSION=${bcversion} -t jssetls-jre-${jreversions[$i]}-bc-1-${bcversion}-server -f Dockerfile .
	i=i+1
done

bcversion=50
jreversion=7u151
docker build --build-arg JRE_VERSION=openjdk:${jreversion}-jre-slim --build-arg BC_VERSION=${bcversion} -t jssetls-jre-${jreversion}-bc-1-${bcversion}-server -f Dockerfile .
