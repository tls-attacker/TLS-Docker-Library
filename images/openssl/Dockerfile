FROM alpine-build as openssl
RUN git clone --depth=1 --branch OpenSSL_1_0_2l https://github.com/openssl/openssl
WORKDIR /src/openssl
RUN ./config --prefix=/build/ --openssldir=/build/ no-async
RUN make && make test && make install

FROM scratch
LABEL "de.rub.nds.tlscompliance.docker.server.name"="openssl"
LABEL "de.rub.nds.tlscompliance.docker.server.version"="1.0.2l"
COPY --from=openssl /lib/ld-musl-x86_64.so.* \
  /usr/lib/libstdc++.so.* \
  /usr/lib/libgcc_s.so.* \
  /build/lib/*.so.* /lib/
COPY --from=openssl /build/bin/openssl /bin/
ENTRYPOINT ["openssl", "s_server"]
