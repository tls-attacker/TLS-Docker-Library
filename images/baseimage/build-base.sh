#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")"

./build-base-image.sh
./build-base-image-ubuntu.sh
