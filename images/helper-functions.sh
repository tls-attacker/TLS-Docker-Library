#!/bin/bash
set -uo pipefail

LOG_FAILED="$(dirname $BASH_SOURCE)/build_failed.log"
LOG_SUCCESS="$(dirname $BASH_SOURCE)/build_succeeded.log"

EXITCODE=0
function _docker {
  tag=$(python - "$@" << E
import sys
s = ['-t', '--tag']
for i in s:
  if i in sys.argv:
    print(sys.argv[sys.argv.index(i)+1])
    sys.exit(0)
E
)

  echo -e "\033[1;33mBuilding $tag...\033[0m"
  if ! outp=$(docker "$@" 2>&1); then
    echo -e "❌\033[1;31m Failed to build $tag!\033[0m"
    EXITCODE=$((EXITCODE + 1))
    echo -e "\n[!-!] Failed to build: $tag\n$outp" >> "$LOG_FAILED"
    return $EXITCODE
  else
    echo -e "✅\033[1;31m Successfully built $tag!\033[0m"
    echo "$tag" >> "$LOG_SUCCESS"
  fi

  return $EXITCODE
}


function exit_on_error {
  trap 'echo >&2 "Error - exited with status $? at line $LINENO:";
         pr -tn $0 | tail -n+$((LINENO - 3)) | head -n7' ERR
  set -euo pipefail
}


function track_error {
  # does not result in an exit, even if set -e is enabled
  if ! "$@"; then
    EXITCODE=$((EXITCODE + 1))
  fi
}
