FROM alpine-build as gnutls-libnettle
RUN git clone --depth=1 --branch nettle_3.3_release_20161001 https://git.lysator.liu.se/nettle/nettle.git
WORKDIR /src/nettle
RUN ./.bootstrap && ./configure --disable-documentation --prefix=/build/
RUN make && make install

FROM alpine-build as gnutls-gnutls
ENV PKG_CONFIG_PATH=/build/lib/pkgconfig/
RUN git clone --depth=1 --branch gnutls_3_5_15 https://github.com/gnutls/gnutls
WORKDIR /src/gnutls/
RUN git submodule update --init --no-fetch
COPY --from=gnutls-libnettle /build/ /build/
RUN make autoreconf
RUN ./configure --with-included-libtasn1 --with-included-unistring --disable-maintainer-mode --disable-doc --disable-full-test-suite --disable-cxx --disable-padlock --without-p11-kit --without-tpm --prefix=/build/
RUN make && make install

FROM scratch as gnutls-base
COPY --from=gnutls-gnutls /lib/ld-musl-x86_64.so.* \
  /usr/lib/libgmp.so.* \
  /usr/lib/libintl.so.* \
  /build/lib/libgnutls.so.* \
  /build/lib/libnettle.so.* \
  /build/lib/libhogweed.so.* \
  /lib/libz.so.* /lib/

FROM gnutls-base as gnutls-server
LABEL "server_type"="gnutls"
LABEL "server_version"="3.5.15"
COPY --from=gnutls-gnutls /build/bin/gnutls-serv /bin/
ENTRYPOINT ["gnutls-serv"]

FROM gnutls-base as gnutls-client
LABEL "client_type"="gnutls"
LABEL "client_version"="3.5.15"
COPY --from=gnutls-gnutls /build/bin/gnutls-cli /bin/
ENTRYPOINT ["gnutls-cli"]
