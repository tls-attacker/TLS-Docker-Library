FROM alpine-build as boringssl-base1
RUN git clone --depth=1 https://boringssl.googlesource.com/boringssl
WORKDIR /build/
RUN cmake -DCMAKE_BUILD_TYPE=Release /src/boringssl/ &&\
  make

FROM scratch as boringssl-base2
COPY --from=boringssl-base1 /lib/ld-musl-x86_64.so.* \
  /usr/lib/libstdc++.so.* \
  /usr/lib/libgcc_s.so.* /lib/
COPY --from=boringssl-base1 /build/tool/bssl \
  /build/ssl/test/bssl_shim /bin/

FROM boringssl-base2 as boringssl-server
LABEL "server_type"="boringssl"
LABEL "server_version"="latest"
ENTRYPOINT ["bssl", "s_server"]

FROM boringssl-base2 as boringssl-client
LABEL "client_type"="boringssl"
LABEL "client_version"="latest"
ENTRYPOINT ["bssl", "s_client"]
