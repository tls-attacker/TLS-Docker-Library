#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

_docker build --tag alpine-build .

#squash does not work on ubuntu

exit "$EXITCODE"
