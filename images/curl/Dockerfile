ARG SSL_BASE
FROM ${SSL_BASE} as ssl-base
# do nothing

FROM alpine-build:3.12 as curl-base
ARG VERSION
RUN wget -O curl.tar.gz https://curl.haxx.se/download/curl-${VERSION}.tar.gz || wget -O curl.tar.gz https://curl.haxx.se/download/archeology/curl-${VERSION}.tar.gz
RUN mkdir curl && tar -xzf curl.tar.gz -C curl --strip-components 1
WORKDIR /src/curl
COPY --from=ssl-base /artifacts/ssllib/ /ssllib/
# ssllib must include following folders: `include`, `lib`
# `--disable-shared` as we do not need a shared library, which caused errors anyway.
RUN CPPFLAGS="-I/ssllib/include" LDFLAGS="-L/ssllib/lib" ./configure --prefix=/build/ --with-ssl=/ssllib/ --disable-shared
RUN make && make install
RUN ls -la /build/*

FROM entrypoint as curl
ARG VERSION
ARG VERSION_LABEL
ARG SSL_BASE
COPY --from=curl-base /build/ /artifacts/curl/
COPY --from=curl-base /build/bin/curl /bin/
COPY --from=curl-base /lib/ld-musl-x86_64.so.* \
    /usr/lib/libstdc++.so.* \
    /usr/lib/libgcc_s.so.* \
    /usr/lib/libgcc_s.so.* \
    /lib/libz.so.* \
    /ssllib/lib/*.so \
    /ssllib/lib/*.so.* \
    /build/lib/*.so.* /lib/
COPY --from=curl-base /bin/sh /bin/
LABEL "tls_implementation"="curl"
LABEL "tls_implementation_version"="${VERSION_LABEL}"
LABEL "tls_implementation_connectionRole"="client"
ENTRYPOINT [ "client-entrypoint", "curl" ]
