#!/bin/bash

tcpdump -i eth0 -U -w /output/dump.pcap &
# wait until target is available
while ! ping -c 1 $TARGET; do
  sleep 1
done

sleep 1
# only perform a single handshake
echo "Q" | openssl s_client -min_protocol SSLv3 -max_protocol TLSv1.3 $@ >> /output/output.log 2>&1
return1=$?

echo "Q" | openssl s_client -min_protocol SSLv3 -max_protocol TLSv1.2 $@ >> /output/output.log 2>&1
return2=$?

sleep 1
kill %1

[[ $return1 -eq 0 ]] || [[ $return2 -eq 0 ]]
exit $?
