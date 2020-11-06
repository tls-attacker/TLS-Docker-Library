ARG SSL_BASE
FROM ${SSL_BASE} as ssl-base
# do nothing

FROM alpine-build:3.12 as curl-base
ARG VERSION
ARG CURL_FLAG="--with-ssl"
RUN wget -O curl.tar.gz https://curl.haxx.se/download/curl-${VERSION}.tar.gz || wget -O curl.tar.gz https://curl.haxx.se/download/archeology/curl-${VERSION}.tar.gz
RUN mkdir curl && tar -xzf curl.tar.gz -C curl --strip-components 1
WORKDIR /src/curl
COPY --from=ssl-base /artifacts/ssllib/ /ssllib/
# ssllib must include following folders: `include`, `lib`
# `--disable-shared` as we do not need a shared library, which caused errors anyway.
ENV LD_LIBRARY_PATH="/ssllib/lib:/ssllib/libdeps"
RUN CPPFLAGS="-I/ssllib/include" LDFLAGS="-L/ssllib/lib" ./configure --prefix=/build/ ${CURL_FLAG}=/ssllib/ --disable-shared | tee _configure.log
RUN grep "SSL: *enabled" _configure.log || ( \
    echo "SSL Support seems to be off"; \
    echo CPPFLAGS="-I/ssllib/include" LDFLAGS="-L/ssllib/lib" ./configure --prefix=/build/ ${CURL_FLAG}=/ssllib/ --disable-shared; \
    cat config.log; \
    cat _configure.log; \
    false)
RUN make && make install
# copy all libs we need (we assume that we also need all the libs our ssllib needed)
# e.g. nss loads some libs dynamically...
RUN cp -r /ssllib/libdeps /libdeps
RUN cp $(ldd /build/bin/curl | awk '$3=="" {print $1}; $3!="" {print $3}') /libdeps/


FROM entrypoint as curl
ARG VERSION_LABEL
COPY --from=curl-base /build/ /artifacts/curl/
COPY --from=curl-base /libdeps/* /artifacts/curl/libdeps/
COPY --from=curl-base /build/bin/curl /bin/
COPY --from=curl-base /libdeps/* /lib/
# very basic check that curl works (or at least is linked properly and libs are found)
RUN ["curl", "--version"]
LABEL "tls_implementation"="curl"
LABEL "tls_implementation_version"="${VERSION_LABEL}"
LABEL "tls_implementation_connectionRole"="client"
ENTRYPOINT [ "client-entrypoint", "curl" ]
