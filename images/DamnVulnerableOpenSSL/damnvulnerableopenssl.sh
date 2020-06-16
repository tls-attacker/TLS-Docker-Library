#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

git clone https://github.com/jurajsomorovsky/DamnVulnerableOpenSSL.git
echo "Building: DamnVulnerableOpenSSL"
cd DamnVulnerableOpenSSL
_docker build --build-arg VERSION=1.0 -t ${DOCKER_REPOSITORY}damnvulnerableopenssl-server:latest -f Dockerfile .

exit "$EXITCODE"
