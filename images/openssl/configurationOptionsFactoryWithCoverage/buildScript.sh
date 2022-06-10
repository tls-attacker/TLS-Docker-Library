#!/bin/sh
# NOTE: Running this script on your machine is pointless. It is only used within a docker container
export CCACHE_DIR=/src/ccache
export CC="gcc --coverage"
/src/openssl/config --prefix=/build/ --openssldir=/build/ no-async no-tests "$@"
make --directory=/src/openssl/ clean
make --directory=/src/openssl/ install_sw
EXIT_CODE=$?

if [ $EXIT_CODE -gt 0 ]
then
    exit $EXIT_CODE
fi

if [ -d "/build/lib/" ]; then
    cp /build/lib/*.so.* /lib/
else
    exit 1
fi

if [ ! -f "/src/openssl/apps/openssl" ]; then
    exit 1
fi

# Remove all non coverage file (but keep the openssl file). Used to keep the final cotainer small.
find /src/openssl/ \( -not -name "*.gcno" -a -not -name "openssl" \) -type f -delete

mkdir /usr/info
printf "Build Date: $(date) \nBuild Parameter: $@" > /usr/info/build_info.txt
exit 0

