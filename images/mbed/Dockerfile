FROM alpine-build as mbed-base1
RUN curl https://tls.mbed.org/download/mbedtls-2.6.0-apache.tgz | tar xvz
WORKDIR /build/
RUN cmake -DCMAKE_BUILD_TYPE=Release /src/mbedtls-2.6.0 &&\
  make

FROM entrypoint as mbed-base2
COPY --from=mbed-base1 /lib/ld-musl-x86_64.so.* /lib/

FROM mbed-base2 as mbed-server
LABEL "tls_library"="mbed"
LABEL "tls_library_version"="2.6.0"
LABEL "tls_library_mode"="server"
COPY --from=mbed-base1 /build/programs/ssl/ssl_server2 /bin/
ENTRYPOINT ["ssl_server2", "server_port=4430"]

FROM mbed-base2 as mbed-client
LABEL "tls_library"="mbed"
LABEL "tls_library_version"="2.6.0"
LABEL "tls_library_mode"="client"
COPY --from=mbed-base1 /build/programs/ssl/ssl_client2 /bin/
ENTRYPOINT ["client-entrypoint", "ssl_client2"]
