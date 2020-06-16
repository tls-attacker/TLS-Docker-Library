#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

array=(v0.8.0-alpha19 v0.8.0-alpha18 v0.8.0-alpha17 v0.8.0-alpha16 v0.8.0-alpha15 v0.8.0-alpha14 v0.8.0-alpha13 v0.8.0-alpha12 v0.8.0-alpha11 v0.8.0-alpha10 v0.8.0-alpha9 v0.8.0-alpha8 v0.8.0-alpha7 v0.8.0-alpha6 v0.8.0-alpha5 v0.8.0-alpha4 v0.8.0-alpha3 v0.8.0-alpha2 v0.8.0-alpha1 v0.7.5 v0.7.4 v0.7.3 v0.7.2 v0.7.1 v0.7.0 v0.7.0-beta1 v0.7.0-alpha9 v0.7.0-alpha8 v0.7.0-alpha7 v0.7.0-alpha6 v0.7.0-alpha5 v0.7.0-alpha4 v0.7.0-alpha3 v0.7.0-alpha2 v0.7.0-alpha1 v0.6.0-beta1 v0.6.0-alpha5 v0.6.0-alpha4 v0.6.0-alpha3 v0.6.0-alpha2 v0.6.0-alpha1 v0.5.2 v0.5.1 v0.5.0 v0.5.0-beta6 v0.5.0-beta4 v0.5.0-beta3 v0.4.8 v0.4.3 v0.4.2 v0.4.1 v0.3.9x fork-start)
typeset -i i=0 max=${#array[*]}

while (( i < max ))
do
	echo "Feld $i: tlslite-ng-${array[$i]}"
	_docker build --build-arg VERSION=${array[$i]} -t ${DOCKER_REPOSITORY}tlslite_ng-server:${array[$i]} -f Dockerfile .
	i=i+1
done

exit "$EXITCODE"
