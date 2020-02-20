#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")"

./mbedtls.sh
./polarssl.sh
