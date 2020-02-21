#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh
exit_on_error

_docker build -t gotls .

exit "$EXITCODE"
exit "$EXITCODE"
