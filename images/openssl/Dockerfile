FROM alpine-build as openssl-base1
RUN git clone --depth=1 --branch OpenSSL_1_0_2l https://github.com/openssl/openssl
WORKDIR /src/openssl
RUN ./config --prefix=/build/ --openssldir=/build/ no-async
RUN make && make test && make install

FROM entrypoint as openssl-base2
COPY --from=openssl-base1 /lib/ld-musl-x86_64.so.* \
  /usr/lib/libstdc++.so.* \
  /usr/lib/libgcc_s.so.* \
  /build/lib/*.so.* /lib/
COPY --from=openssl-base1 /build/bin/openssl /bin/

FROM openssl-base2 as openssl-server
LABEL "tls_library"="openssl"
LABEL "tls_library_version"="1.0.2l"
LABEL "tls_library_mode"="server"
ENTRYPOINT ["openssl", "s_server"]

FROM openssl-base2 as openssl-client
LABEL "tls_library"="openssl"
LABEL "tls_library_version"="1.0.2l"
LABEL "tls_library_mode"="client"
ENTRYPOINT ["client-entrypoint", "openssl", "s_client"]
