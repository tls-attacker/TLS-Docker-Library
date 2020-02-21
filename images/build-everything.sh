#!/bin/bash
cd "$(dirname "$0")" || exit 1
source ./helper-functions.sh
exit_on_error

echo "" > "$LOG_SUCCESS"
echo "" > "$LOG_FAILED"

./baseimage/build-base.sh

results=()
while IFS=  read -r -d $'\0'; do
    results+=("$REPLY")
done < <(find . -type f -name "build.sh" -print0)

len=${#results[@]}
for (( i=0; i<len; i++ )); do
  n=$((i + 1))
  echo -e "\033[1;33m${n}/${len}, building $(dirname "${results[$i]}")...\033[0m"
  if ! ${results[$i]} 2>&1; then
    echo -e "\033[1;31m${n}/${len}, failed building $(dirname "${results[$i]}")!\033[0m"
  else
    echo -e "\033[1;32m${n}/${len}, succeeded building $(dirname "${results[$i]}")!\033[0m"
  fi

  echo " "
done
