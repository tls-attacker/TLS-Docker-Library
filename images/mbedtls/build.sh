#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh

exit_on_error

track_error ./mbedtls.sh
track_error ./polarssl.sh
track_error ./mbedtls_post_2.16.6.sh

exit "$EXITCODE"
