FROM alpine-build:3.12 as python-mbedtls
ARG VERSION
RUN apk add --no-cache mbedtls-dev py-setuptools python2-dev
RUN curl https://bootstrap.pypa.io/get-pip.py -o get-pip.py
RUN python get-pip.py
RUN pip install contextlib2 pytest enum34 ipaddress certifi 
RUN git clone --depth=1 --branch=${VERSION} https://github.com/Synss/python-mbedtls
WORKDIR python-mbedtls
RUN python setup.py build && python setup.py install

FROM entrypoint as entrypoint

FROM python:2.7-alpine
ARG VERSION
LABEL "tls_implementation"="python-mbedtls"
LABEL "tls_implementation_version"="${VERSION}"
LABEL "tls_implementation_connectionRole"="server"
COPY --from=python-mbedtls /src/python-mbedtls/tests /src/
COPY tls_server.py /src/
COPY --from=python-mbedtls /usr/lib/python2.7/site-packages/easy-install.pth /usr/lib/python2.7/site-packages/python_mbedtls-${VERSION}-py2.7-linux-x86_64.egg /usr/local/lib/python2.7/site-packages/
COPY --from=python-mbedtls /usr/lib/libmbed* /usr/local/lib/
COPY --from=python-mbedtls /usr/lib/python2.7/site-packages/contextlib2.pyc /usr/local/lib/python2.7/site-packages/
COPY --from=python-mbedtls /usr/lib/python2.7/site-packages/enum/ /usr/local/lib/python2.7/site-packages/enum/
COPY --from=python-mbedtls /usr/lib/python2.7/site-packages/certifi/ /usr/local/lib/python2.7/site-packages/certifi/
COPY --from=python-mbedtls /usr/lib/python2.7/site-packages/ipaddress.pyc /usr/local/lib/python2.7/site-packages/
COPY --from=entrypoint /bin/server-entrypoint /bin/
WORKDIR /src
ENTRYPOINT ["server-entrypoint", "python","tls_server.py"]


