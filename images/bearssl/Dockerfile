FROM alpine-build as bearssl-base1
RUN git clone --depth=1 --branch=v0.5 https://www.bearssl.org/git/BearSSL
WORKDIR BearSSL
RUN make

FROM scratch as bearssl-base2
COPY --from=bearssl-base1 /lib/ld-musl-x86_64.so.1 /lib/
COPY --from=bearssl-base1 /src/BearSSL/build/brssl /bin/

FROM bearssl-base2 as bearssl-server
LABEL "server_type"="bearssl"
LABEL "server_version"="0.5"
ENTRYPOINT ["brssl", "server"]

FROM bearssl-base2 as bearssl-client
LABEL "client_type"="bearssl"
LABEL "client_version"="0.5"
ENTRYPOINT ["brssl", "client"]
