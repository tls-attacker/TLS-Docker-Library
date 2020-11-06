#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ../helper-functions.sh
exit_on_error

track_error ./curl.sh
track_error ./curl_package_manager.sh
track_error ./curl_lib_package_manager.sh

exit "$EXITCODE"
