FROM alpine-build:3.12 as bearssl-base1
ARG VERSION
RUN git clone --depth=1 --branch=${VERSION} https://www.bearssl.org/git/BearSSL
WORKDIR BearSSL
RUN make

FROM entrypoint as bearssl-base2
COPY --from=bearssl-base1 /lib/ld-musl-x86_64.so.1 /lib/
COPY --from=bearssl-base1 /src/BearSSL/build/brssl /bin/

FROM bearssl-base2 as bearssl-server
ARG VERSION
LABEL "tls_implementation"="bearssl"
LABEL "tls_implementation_version"="${VERSION}"
LABEL "tls_implementation_connectionRole"="server"
ENTRYPOINT ["server-entrypoint", "brssl", "server"]

FROM bearssl-base2 as bearssl-client
ARG VERSION
LABEL "tls_implementation"="bearssl"
LABEL "tls_implementation_version"="${VERSION}"
LABEL "tls_implementation_connectionRole"="client"
ENTRYPOINT ["client-entrypoint", "brssl", "client"]
