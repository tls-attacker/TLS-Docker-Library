FROM alpine-build as botan
RUN git clone --depth=1 --branch 2.2.0 https://github.com/randombit/botan
WORKDIR botan
RUN ./configure.py --prefix=/build/ &&\
  make &&\
  make install

FROM scratch
LABEL "de.rub.nds.tlscompliance.docker.server.name"="botan"
LABEL "de.rub.nds.tlscompliance.docker.server.version"="2.2.0"
COPY --from=botan /lib/ld-musl-x86_64.so.1 \
  /usr/lib/libstdc++.so.6 \
  /usr/lib/libgcc_s.so.1 \
  /build/lib/libbotan-2.so.2 /lib/
COPY --from=botan /build/bin/botan /bin/
ENTRYPOINT ["botan", "tls_server"]
