#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")"

./botan-1_11_X.sh
./botan-2_X.sh
./botan-2_X_0.sh
