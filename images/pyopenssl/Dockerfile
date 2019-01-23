FROM alpine-build as py-openssl
ARG VERSION
RUN apk add --no-cache py-setuptools libffi-dev python-dev openssl-dev
RUN git clone --depth=1 --branch=${VERSION} https://github.com/pyca/pyopenssl
WORKDIR pyopenssl
RUN python setup.py build && python setup.py install
WORKDIR examples
RUN python mk_simple_certs.py

FROM python:2.7-alpine
ARG VERSION
LABEL "server_type"="py-openssl-${VERSION}"
LABEL "server_version"="${VERSION}"
COPY --from=py-openssl /src/pyopenssl/examples/* /src/
COPY --from=py-openssl /src/pyopenssl/examples/simple/* /src/simple/
COPY --from=py-openssl /usr/lib/python2.7/site-packages/*.egg /usr/local/lib/python2.7/site-packages/
COPY --from=py-openssl /usr/lib/python2.7/site-packages/easy-install.pth /usr/local/lib/python2.7/site-packages/
COPY --from=py-openssl /usr/lib/libffi.so.6 /usr/lib/libssl.so.1.0.0 /usr/lib/libcrypto.so.1.0.0 /usr/local/lib/
COPY server.py /src/simple/
WORKDIR src/simple
ENTRYPOINT ["python","server.py"]


