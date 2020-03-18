#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh
exit_on_error

track_error ./openssl-0_9_X.sh
track_error ./openssl-1_0_0X.sh
track_error ./openssl-1_0_1X.sh
track_error ./openssl-1_0_2X.sh
track_error ./openssl-1_1_0X.sh
track_error ./openssl-1_1_1X.sh

exit "$EXITCODE"
