FROM alpine-build as wolfssl
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


#--disable-filesystem &&\

FROM scratch
LABEL "server_type"="wolfssl"
LABEL "server_version"="3.12.0"
COPY --from=wolfssl /build/lib/libwolfssl.so.12.1.0 \
  /lib/ld-musl-x86_64.so.1 /lib/
COPY --from=wolfssl /src/wolfssl/src/.libs/libwolfssl.so.12 /src/wolfssl/src/.libs/libwolfssl.so.12
COPY --from=wolfssl /src/wolfssl/certs /certs/
COPY --from=wolfssl /src/wolfssl/examples/server/.libs/lt-server /bin/
ENTRYPOINT ["lt-server"]
