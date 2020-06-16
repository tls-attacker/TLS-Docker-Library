FROM alpine-build as matrixssl-base1
RUN git clone --depth=1 --branch=3-9-3-open https://github.com/matrixssl/matrixssl
RUN (cd matrixssl &&\
      make libs)
RUN (cd matrixssl/apps/ssl &&\
      make)

FROM scratch as matrixssl-base2
COPY --from=matrixssl-base1 /lib/ld-musl-x86_64.so.* /lib/

FROM matrixssl-base2 as matrixssl-server
LABEL "tls_library"="matrixssl"
LABEL "tls_library_version"="3.9.3"
LABEL "tls_library_mode"="server"
COPY --from=matrixssl-base1 /src/matrixssl/apps/ssl/server /bin/
COPY --from=matrixssl-base1 /src/matrixssl/testkeys /testkeys
ENTRYPOINT ["server"]

FROM matrixssl-base2 as matrixssl-client
LABEL "tls_library"="matrixssl"
LABEL "tls_library_version"="3.9.3"
LABEL "tls_library_mode"="client"
COPY --from=matrixssl-base1 /src/matrixssl/apps/ssl/client /bin/
ENTRYPOINT ["client"]
