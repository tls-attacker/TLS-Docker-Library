FROM alpine-build as botan-base1
RUN git clone --depth=1 --branch 2.2.0 https://github.com/randombit/botan
WORKDIR botan
RUN ./configure.py --prefix=/build/ &&\
  make &&\
  make install

FROM scratch as botan-base2
COPY --from=botan-base1 /lib/ld-musl-x86_64.so.1 \
  /usr/lib/libstdc++.so.6 \
  /usr/lib/libgcc_s.so.1 \
  /build/lib/libbotan-2.so.2 /lib/
COPY --from=botan-base1 /build/bin/botan /bin/
ADD https://raw.githubusercontent.com/randombit/botan/master/src/tests/data/tls-policy/compat.txt /compat.txt

FROM botan-base2 as botan-server
LABEL "tls_library"="botan"
LABEL "tls_library_version"="2.2.0"
LABEL "tls_library_mode"="server"
ENTRYPOINT ["botan", "tls_server"]

FROM botan-base2 as botan-client
LABEL "tls_library"="botan"
LABEL "tls_library_version"="2.2.0"
LABEL "tls_library_mode"="client"
ENTRYPOINT ["botan", "tls_client"]
