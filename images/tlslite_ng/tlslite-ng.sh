#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

versions=(0.8.0-alpha39 0.8.0-alpha38 0.8.0-alpha37 0.8.0-alpha36 0.8.0-alpha35 0.8.0-alpha34 0.8.0-alpha33 0.8.0-alpha32 0.8.0-alpha31 0.8.0-alpha30 0.8.0-alpha29 0.8.0-alpha28 0.8.0-alpha27 0.8.0-alpha26 0.8.0-alpha25 0.8.0-alpha24 0.8.0-alpha23 0.8.0-alpha22 0.8.0-alpha21 0.8.0-alpha20 0.8.0-alpha19 0.8.0-alpha18 0.8.0-alpha17 0.8.0-alpha16 0.8.0-alpha15 0.8.0-alpha14 0.8.0-alpha13 0.8.0-alpha12 0.8.0-alpha11 0.8.0-alpha10 0.8.0-alpha9 0.8.0-alpha8 0.8.0-alpha7 0.8.0-alpha6 0.8.0-alpha5 0.8.0-alpha4 0.8.0-alpha3 0.8.0-alpha2 0.8.0-alpha1 0.7.6 0.7.5 0.7.4 0.7.3 0.7.2 0.7.1 0.7.0 0.7.0-beta1 0.7.0-alpha9 0.7.0-alpha8 0.7.0-alpha7 0.7.0-alpha6 0.7.0-alpha5 0.7.0-alpha4 0.7.0-alpha3 0.7.0-alpha2 0.7.0-alpha1 0.6.0 0.6.0-beta1 0.6.0-alpha5 0.6.0-alpha4 0.6.0-alpha3 0.6.0-alpha2 0.6.0-alpha1 0.5.2 0.5.1 0.5.0 0.5.0-beta6 0.5.0-beta4 0.5.0-beta3)
for i in "${versions[@]}"; do
	echo "Feld: tlslite-ng-${i}"
	_docker build --build-arg VERSION=${i} -t ${DOCKER_REPOSITORY}tlslite_ng-server:${i} -f Dockerfile_python3 --target tlslite-ng-server .
	_docker build --build-arg VERSION=${i} -t ${DOCKER_REPOSITORY}tlslite_ng-client:${i} -f Dockerfile_python3 --target tlslite-ng-client .
    if [ ! -z "$DOCKER_REPOSITORY" ]; then
        docker push ${DOCKER_REPOSITORY}tlslite_ng-server:${i}
        docker push ${DOCKER_REPOSITORY}tlslite_ng-client:${i}
    fi
done

versions=(0.4.8 0.4.3 0.4.2 0.4.1 0.3.9x)
for i in "${versions[@]}"; do
	echo "Feld: tlslite-ng-${i}"
	_docker build --build-arg VERSION=${i} -t ${DOCKER_REPOSITORY}tlslite_ng-server:${i} -f Dockerfile_python2 --target tlslite-ng-server .
	_docker build --build-arg VERSION=${i} -t ${DOCKER_REPOSITORY}tlslite_ng-client:${i} -f Dockerfile_python2 --target tlslite-ng-client .
    if [ ! -z "$DOCKER_REPOSITORY" ]; then
        docker push ${DOCKER_REPOSITORY}tlslite_ng-server:${i}
        docker push ${DOCKER_REPOSITORY}tlslite_ng-client:${i}
    fi
done


exit "$EXITCODE"
