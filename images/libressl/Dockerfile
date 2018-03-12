FROM alpine-build as libressl
RUN git clone --depth=1 --branch v2.6.1 https://github.com/libressl-portable/portable
WORKDIR portable
RUN ./autogen.sh &&\
  ./configure &&\
  make &&\
  DESTDIR=/build/ make install

FROM scratch
LABEL "de.rub.nds.tlscompliance.docker.server.name"="libressl"
LABEL "de.rub.nds.tlscompliance.docker.server.version"="2.6.1"
COPY --from=libressl /lib/ld-musl-x86_64.so.* \
  /build/usr/local/lib/*.so* /lib/
COPY --from=libressl /build/usr/local/bin/openssl /bin/
ENTRYPOINT ["openssl", "s_server"]
