#!/bin/bash
set -uo pipefail

FOLDER="$(realpath "$(dirname "$BASH_SOURCE")")"
DOCKER_REPOSITORY="${DOCKER_REPOSITORY:-}"

# if set to 1, docker commands are recorded and written to cmds.sh
# required by build-everythin.py
CMD_GENERATION_MODE="${CMD_GENERATION_MODE:-0}"

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

  if [[ $CMD_GENERATION_MODE -eq 0 ]]; then
    echo -e "\033[1;33mBuilding $tag...\033[0m"
    if ! outp=$(docker "$@" 2>&1); then
      echo -e "[-]\033[1;31m Failed to build $tag!\033[0m\n$outp"
      EXITCODE=$((EXITCODE + 1))
      return $EXITCODE
    else
      echo -e "[+]\033[1;32m Successfully built $tag!\033[0m"
    fi
  else
    echo "cd $(pwd)" >> "$FOLDER/cmds.sh"
    echo docker "$@" >> "$FOLDER/cmds.sh"
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
