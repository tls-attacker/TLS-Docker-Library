FROM alpine-build as matrixssl
RUN git clone --depth=1 --branch=3-9-3-open https://github.com/matrixssl/matrixssl
RUN (cd matrixssl &&\
      make libs)
RUN (cd matrixssl/apps/ssl &&\
      make)

FROM scratch
LABEL "server_type"="matrixssl"
LABEL "server_version"="3.9.3"
COPY --from=matrixssl /lib/ld-musl-x86_64.so.* /lib/
COPY --from=matrixssl /src/matrixssl/apps/ssl/server /bin/
COPY --from=matrixssl /src/matrixssl/testkeys /testkeys
ENTRYPOINT ["server"]
