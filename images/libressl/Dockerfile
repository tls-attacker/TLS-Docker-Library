FROM alpine-build as libressl
RUN git clone --depth=1 --branch v2.6.1 https://github.com/libressl-portable/portable
WORKDIR portable
RUN ./autogen.sh &&\
  ./configure &&\
  make &&\
  DESTDIR=/build/ make install

FROM scratch as libressl-base
COPY --from=libressl /lib/ld-musl-x86_64.so.* \
  /build/usr/local/lib/*.so* /lib/
COPY --from=libressl /build/usr/local/bin/openssl /bin/

FROM libressl-base as libressl-server
LABEL "tls_library"="libressl"
LABEL "tls_library_version"="2.6.1"
LABEL "tls_library_mode"="server"
ENTRYPOINT ["openssl", "s_server"]

FROM libressl-base as libressl-client
LABEL "tls_library"="libressl"
LABEL "tls_library_version"="2.6.1"
LABEL "tls_library_mode"="client"
ENTRYPOINT ["openssl", "s_client"]
