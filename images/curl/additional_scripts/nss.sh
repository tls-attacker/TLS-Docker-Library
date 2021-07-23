#!/bin/sh
set -e

echo "Building nss-pem"
cd /src
git clone https://github.com/kdudka/nss-pem
cd nss-pem
mkdir build
cd build
echo "running cmake"
cat >/ssllib/nss.pc <<EOF
Name: NSS
Description: Network Security Services
Version: ?
Libs: -L/ssllib/lib -L/libdeps -lssl3 -lsmime3 -lnss3 -lnssutil3
Cflags: -I/ssllib/include -I/ssllib/dist-private/nss
EOF
ls -la /ssllib/nss.pc
PKG_CONFIG_PATH="/ssllib" cmake ../src
echo "running make"
make -j VERBOSE=1
echo "ensuring dependent libs are found"
ldd libnsspem.so
echo "Done building nss-pem"
cp libnsspem.so /libdeps/
cp $(ldd libnsspem.so | awk '$3=="" {print $1}; $3!="" {print $3}') /libdeps/

