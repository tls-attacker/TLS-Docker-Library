FROM alpine-build:3.12 as nss
ENV BUILD_OPT="1"
ENV USE_64="1"
RUN hg clone -r NSS_3_32_RTM https://hg.mozilla.org/projects/nss
RUN hg clone -r NSPR_4_16_RTM https://hg.mozilla.org/projects/nspr
WORKDIR /src/nss
RUN make nss_build_all install

FROM scratch
LABEL "tls_library"="nss"
LABEL "tls_library_version"="3.32"
LABEL "tls_library_mode"="server"
COPY --from=nss /lib/ld-musl-x86_64.so.* \
  /lib/libz.so.* \
  /usr/lib/libstdc++.so.* \
  /usr/lib/libgcc_s.so.* \
  /src/dist/Linux*/lib/lib*.so /lib/
COPY --from=nss /src/dist/Linux*/bin/selfserv \
  /src/dist/Linux*/bin/nss_bogo_shim /bin/
ENTRYPOINT ["/bin/selfserv"]
