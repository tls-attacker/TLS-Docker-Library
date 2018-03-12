FROM rust as rustls
WORKDIR /src/
RUN git clone --depth=1 https://github.com/ctz/rustls
WORKDIR rustls
RUN cargo build --release --example tlsserver

FROM scratch
LABEL "de.rub.nds.tlscompliance.docker.server.name"="rustls"
LABEL "de.rub.nds.tlscompliance.docker.server.version"="latest"
COPY --from=rustls /lib/x86_64-linux-gnu/libdl.so.2 \
  /lib/x86_64-linux-gnu/librt.so.1 \
  /lib/x86_64-linux-gnu/libpthread.so.0 \
  /lib/x86_64-linux-gnu/libgcc_s.so.1 \
  /lib/x86_64-linux-gnu/libc.so.6 /lib/
COPY --from=rustls /lib64/ld-linux-x86-64.so.2 /lib64/
COPY --from=rustls /src/rustls/target/release/examples/tlsserver /bin/
ENTRYPOINT ["tlsserver"]
