#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")"

./cyassl.sh
./wolfssl.sh
