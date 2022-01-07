#!/bin/sh
# NOTE: Running this script on your machine is pointless. It is only used within a docker container
export CCACHE_DIR=/src/ccache
# -fprofile-dir   ???
export CC="gcc --coverage"
/src/openssl/config --prefix=/build/ --openssldir=/build/ no-async no-tests "$@"
make --directory=/src/openssl/ clean
make --directory=/src/openssl/ install_sw

cp /build/lib/*.so.* /lib/

mkdir /usr/info
printf "Build Date: $(date) \nBuild Parameter: $@" > /usr/info/build_info.txt

