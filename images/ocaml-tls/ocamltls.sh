#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

_docker build -t ocamltls-0.8.0-server --target ocamltls-server .
_docker build -t ocamltls-0.8.0-client --target ocamltls-client .

exit "$EXITCODE"
