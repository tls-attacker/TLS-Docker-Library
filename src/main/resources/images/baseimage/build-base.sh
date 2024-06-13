#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh
exit_on_error

track_error ./build-base-image.sh

exit "$EXITCODE"
