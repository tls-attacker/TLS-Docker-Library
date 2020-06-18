#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

# https://wiki.alpinelinux.org/wiki/Alpine_Linux:Releases
_docker build --build-arg VERSION=3.12 -t alpine-build .
_docker build --build-arg VERSION=3.11 -t alpine-build:3.11 .
_docker build --build-arg VERSION=3.10 -t alpine-build:3.10 .
_docker build --build-arg VERSION=3.9 -t alpine-build:3.9 .
_docker build --build-arg VERSION=3.8 -t alpine-build:3.8 .
_docker build --build-arg VERSION=3.7 -t alpine-build:3.7 .
_docker build --build-arg VERSION=3.6 -t alpine-build:3.6 .
_docker build --build-arg VERSION=3.5 -t alpine-build:3.5 .
_docker build --build-arg VERSION=3.4 -t alpine-build:3.4 .

exit "$EXITCODE"
