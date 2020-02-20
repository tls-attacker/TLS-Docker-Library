#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")"

./baseimage/build-base.sh

results=()
while IFS=  read -r -d $'\0'; do
    results+=("$REPLY")
done < <(find . -type f -name "build.sh" -print0)

len=${#results[@]}
for (( i=0; i<len; i++ )); do
  n=$((i + 1))
  echo -e "\033[1;33m${n}/${len}, building $(dirname "${results[$i]}")...\033[0m"
  if ! outp=$(${results[$i]} 2>&1); then
    echo "$outp"
    echo -e "\033[1;31m${n}/${len}, failed to build $(dirname "${results[$i]}")!\033[0m"
  else
    echo -e "\033[1;32m${n}/${len}, building of $(dirname "${results[$i]}") completed!\033[0m"
  fi

  echo " "
done


