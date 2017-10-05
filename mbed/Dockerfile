FROM alpine-build as mbed
RUN curl https://tls.mbed.org/download/mbedtls-2.6.0-apache.tgz | tar xvz
WORKDIR /build/
RUN cmake -DCMAKE_BUILD_TYPE=Release /src/mbedtls-2.6.0 &&\
  make

FROM scratch
LABEL "de.rub.nds.tlscompliance.docker.server.name"="mbed"
LABEL "de.rub.nds.tlscompliance.docker.server.version"="2.6.0"
COPY --from=mbed /lib/ld-musl-x86_64.so.* /lib/
COPY --from=mbed /build/programs/ssl/ssl_server2 /bin/
ENTRYPOINT ["ssl_server2", "server_port=4430"]
