#!/bin/bash -e
cd "$(dirname "$0")" || exit 1

echo "[+] Generate certificates"
./certs/setup.sh

echo "[+] Build base image"
./src/main/resources/images/baseimage/build-base.sh

echo " "
echo "To build every available docker image, or every docker image of a specific TLS Libraries, use the 'build-everything.py' script (requires python >=3.7)"
echo "To build only specific TLS Libraries, use the 'build.sh' scripts inside the subfolders of 'src/main/resources/images/'."
