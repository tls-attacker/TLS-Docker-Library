FROM rust as rustls-base1
ARG VERSION
WORKDIR /src/
RUN git clone --branch=v/${VERSION} --depth=1 https://github.com/ctz/rustls
WORKDIR rustls/rustls-mio
RUN cargo build --release --example tlsserver --features "dangerous_configuration"
RUN cargo build --release --example tlsclient --features "dangerous_configuration"

FROM debian:stable-slim as rustls-base2
FROM entrypoint as entrypoint

FROM rustls-base2 as rustls-server
ARG VERSION
LABEL "tls_implementation"="rustls"
LABEL "tls_implementation_version"="${VERSION}"
LABEL "tls_implementation_connectionRole"="server"
COPY --from=rustls-base1 /src/rustls/target/release/examples/tlsserver /usr/local/bin/
ENTRYPOINT ["server-entrypoint", "tlsserver"]

FROM rustls-base2 as rustls-client
ARG VERSION
LABEL "tls_implementation"="rustls"
LABEL "tls_implementation_version"="${VERSION}"
LABEL "tls_implementation_connectionRole"="client"
COPY --from=rustls-base1 /src/rustls/target/release/examples/tlsclient /usr/local/bin/
COPY --from=entrypoint /bin/client-entrypoint /usr/local/bin
ENTRYPOINT ["client-entrypoint", "tlsclient"]
