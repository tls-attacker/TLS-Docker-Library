FROM alpine-build as bearssl
RUN git clone --depth=1 --branch=v0.5 https://www.bearssl.org/git/BearSSL
WORKDIR BearSSL
RUN make

FROM scratch
LABEL "de.rub.nds.tlscompliance.docker.server.name"="bearssl"
LABEL "de.rub.nds.tlscompliance.docker.server.version"="0.5"
COPY --from=bearssl /lib/ld-musl-x86_64.so.1 /lib/
COPY --from=bearssl /src/BearSSL/build/brssl /bin/
ENTRYPOINT ["brssl", "server"]
