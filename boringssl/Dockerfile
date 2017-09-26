FROM alpine-build as boringssl
RUN git clone --depth=1 https://boringssl.googlesource.com/boringssl
WORKDIR /build/
RUN cmake -DCMAKE_BUILD_TYPE=Release /src/boringssl/ &&\
  make

FROM scratch
COPY --from=boringssl /lib/ld-musl-x86_64.so.* \
  /usr/lib/libstdc++.so.* \
  /usr/lib/libgcc_s.so.* /lib/
COPY --from=boringssl /build/tool/bssl \
  /build/ssl/test/bssl_shim /bin/
ENTRYPOINT ["bssl", "s_server"]
