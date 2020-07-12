FROM debian-build:sid as s2n-base1
ARG VERSION
RUN apt-get install -y libssl-dev
RUN git clone --branch=v${VERSION} --depth=1 https://github.com/awslabs/s2n
WORKDIR s2n
RUN make bin

FROM debian:stable-slim as s2n-base2
ENV S2N_DONT_MLOCK=1
COPY --from=s2n-base1 \
  /usr/lib/x86_64-linux-gnu/libcrypto.so.1.1 \
  /src/s2n/lib/libs2n.so /lib/x86_64-linux-gnu/

FROM entrypoint as entrypoint

FROM s2n-base2 as s2n-server
ARG VERSION
LABEL "tls_implementation"="s2n"
LABEL "tls_implementation_version"="${VERSION}"
LABEL "tls_implementation_connectionRole"="server"
COPY --from=s2n-base1 /src/s2n/bin/s2nd /usr/local/bin/
COPY --from=entrypoint /bin/server-entrypoint /usr/local/bin/
ENTRYPOINT ["server-entrypoint", "s2nd"]

FROM s2n-base2 as s2n-client
ARG VERSION
LABEL "tls_implementation"="s2n"
LABEL "tls_implementation_version"="${VERSION}"
LABEL "tls_implementation_connectionRole"="client"
ENV S2N_ENABLE_CLIENT_MODE=1
COPY --from=s2n-base1 /src/s2n/bin/s2nc /usr/local/bin/
COPY --from=entrypoint /bin/client-entrypoint /usr/local/bin/
ENTRYPOINT ["client-entrypoint", "s2nc"]

