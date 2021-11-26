#!/bin/sh
# NOTE: Running this script on your machine is pointless. It is only used within a docker container
export CCACHE_DIR=/src/ccache
/src/openssl/config --prefix=/build/ --openssldir=/build/ no-async no-tests "$@"
make --directory=/src/openssl/ -s clean install_sw 