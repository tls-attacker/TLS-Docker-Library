FROM alpine-build:3.12 as nss
ARG NSS_VERSION
ARG NSPR_VERSION
RUN hg clone -r NSS_${NSS_VERSION} https://hg.mozilla.org/projects/nss
RUN hg clone -r NSPR_${NSPR_VERSION} https://hg.mozilla.org/projects/nspr
RUN apk add py3-pip && \
    apk add ninja && \
    ln -s /usr/bin/python3 /usr/bin/python && \
    pip install setuptools && \
    git clone https://chromium.googlesource.com/external/gyp && \
    cd gyp && python3 ./setup.py install
WORKDIR /src/nss
RUN ./build.sh

FROM entrypoint as nss-base
COPY --from=nss /lib/ld-musl-x86_64.so.* \
  /lib/libz.so.* \
  /usr/lib/libstdc++.so.* \
  /usr/lib/libgcc_s.so.* \
  /src/dist/Debug/lib/* /lib/
COPY --from=nss \
  /src/dist/Debug/bin/selfserv \
  /src/dist/Debug/bin/tstclnt \
  /src/dist/Debug/bin/nss_bogo_shim /bin/

FROM nss-base as nss-server
ARG VERSION
LABEL "tls_implementation"="nss"
LABEL "tls_implementation_version"="${VERSION}"
LABEL "tls_implementation_connectionRole"="server"
ENTRYPOINT ["server-entrypoint", "/bin/selfserv"]

FROM nss-base as nss-client
ARG VERSION
LABEL "tls_implementation"="nss"
LABEL "tls_implementation_version"="${VERSION}"
LABEL "tls_implementation_connectionRole"="client"
ENTRYPOINT ["client-entrypoint", "/bin/tstclnt"]
