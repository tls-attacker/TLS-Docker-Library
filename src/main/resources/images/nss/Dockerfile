FROM alpine-build:3.12 AS nss
ARG NSS_VERSION
ARG NSPR_VERSION
RUN hg clone -r NSS_${NSS_VERSION} https://hg.mozilla.org/projects/nss && \
  hg clone -r NSPR_${NSPR_VERSION} https://hg.mozilla.org/projects/nspr
RUN apk add --no-cache py3-pip ninja && \
  ln -s /usr/bin/python3 /usr/bin/python && \
  pip install --no-cache-dir setuptools && \
  git clone --depth=1 https://chromium.googlesource.com/external/gyp && \
  cd gyp && python3 ./setup.py install
WORKDIR /src/nss
RUN ./build.sh --opt
# copy all libs we need
# just add all other libs, as some are loaded dynamically...
RUN mkdir /libdeps && \
  cp $(LD_LIBRARY_PATH="/src/dist/Release/lib/" ldd /src/dist/Release/bin/selfserv | awk '$3=="" {print $1}; $3!="" {print $3}') /libdeps/ && \
  cp $(LD_LIBRARY_PATH="/src/dist/Release/lib/" ldd /src/dist/Release/bin/tstclnt | awk '$3=="" {print $1}; $3!="" {print $3}') /libdeps/ && \
  cp -r /src/nss/out/Release/* /libdeps/ && \
  cp -r /src/dist/Release/lib/* /libdeps/

FROM entrypoint AS nss-base
COPY --from=nss /src/dist/public/nss/ /src/dist/Release/include/nspr /artifacts/ssllib/include/
COPY --from=nss /src/dist/Release/lib/ /artifacts/ssllib/lib/
COPY --from=nss /src/dist/private/ /artifacts/ssllib/dist-private/
COPY --from=nss /libdeps/* /artifacts/ssllib/libdeps/
COPY --from=nss /libdeps/* /lib/
COPY --from=nss \
  /src/dist/Release/bin/selfserv \
  /src/dist/Release/bin/tstclnt \
  /src/dist/Release/bin/nss_bogo_shim /bin/

FROM nss-base AS nss-server
ARG VERSION
LABEL "tls_implementation"="nss"
LABEL "tls_implementation_version"="${VERSION}"
LABEL "tls_implementation_connectionRole"="server"
ENTRYPOINT ["server-entrypoint", "/bin/selfserv"]

FROM nss-base AS nss-client
ARG VERSION
LABEL "tls_implementation"="nss"
LABEL "tls_implementation_version"="${VERSION}"
LABEL "tls_implementation_connectionRole"="client"
ENTRYPOINT ["client-entrypoint", "/bin/tstclnt"]
