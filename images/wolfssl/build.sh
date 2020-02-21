#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh
exit_on_error

track_error ./cyassl.sh
track_error ./wolfssl.sh

exit "$EXITCODE"
