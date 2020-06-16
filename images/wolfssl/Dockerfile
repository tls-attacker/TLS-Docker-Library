FROM alpine-build as wolfssl-base1
RUN git clone --depth=1 --branch=v3.12.0-stable https://github.com/wolfSSL/wolfssl
RUN (cd wolfssl &&\
      ./autogen.sh &&\
      ./configure --prefix=/build/ &&\
      make &&\
      make install)
WORKDIR /src/wolfssl/examples/server
#this script is a shell script, which creates (links) the lt-server executable on the fly
#we need this to copy is later to the execution container
RUN /src/wolfssl/examples/server/server -h
RUN /src/wolfssl/examples/client/client -h

#--disable-filesystem &&\

FROM scratch as wolfssl-base2
COPY --from=wolfssl-base1 /build/lib/libwolfssl.so.12.1.0 \
  /lib/ld-musl-x86_64.so.1 /lib/
COPY --from=wolfssl-base1 /src/wolfssl/src/.libs/libwolfssl.so.12 /src/wolfssl/src/.libs/libwolfssl.so.12
COPY --from=wolfssl-base1 /src/wolfssl/certs /certs/

FROM wolfssl-base2 as wolfssl-server
LABEL "tls_library"="wolfssl"
LABEL "tls_library_version"="3.12.0"
LABEL "tls_library_mode"="server"
COPY --from=wolfssl-base1 /src/wolfssl/examples/server/.libs/lt-server /bin/
ENTRYPOINT ["lt-server"]

FROM wolfssl-base2 as wolfssl-client
LABEL "tls_library"="wolfssl"
LABEL "tls_library_version"="3.12.0"
LABEL "tls_library_mode"="client"
COPY --from=wolfssl-base1 /src/wolfssl/examples/client/.libs/lt-client /bin/
ENTRYPOINT ["lt-client"]
