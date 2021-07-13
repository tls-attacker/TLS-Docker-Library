#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh
exit_on_error

track_error ./botan-1_11_X.sh
track_error ./botan-2_X.sh

exit "$EXITCODE"
